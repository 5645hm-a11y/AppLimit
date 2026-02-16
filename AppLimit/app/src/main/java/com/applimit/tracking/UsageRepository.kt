package com.applimit.tracking

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.DayOfWeek

class UsageRepository private constructor(private val context: Context) {
    private val dao = UsageDatabase.getInstance(context).usageDao()

    suspend fun addUsageSession(packageName: String, startMs: Long, endMs: Long) {
        if (packageName.isBlank() || endMs <= startMs) return
        withContext(Dispatchers.IO) {
            try {
                val zone = ZoneId.systemDefault()
                var cursor = startMs
                while (cursor < endMs) {
                    val date = Instant.ofEpochMilli(cursor).atZone(zone).toLocalDate()
                    val nextDayStart = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
                    val segmentEnd = minOf(endMs, nextDayStart)
                    val delta = segmentEnd - cursor
                    val dateKey = date.toString()

                    val updated = dao.addUsage(packageName, dateKey, delta)
                    if (updated == 0) {
                        dao.insert(UsageEntry(packageName, dateKey, delta))
                    }

                    cursor = segmentEnd
                }
            } catch (e: Exception) {
                Log.e("UsageRepository", "Failed to add usage session: ${e.message}", e)
            }
        }
    }

    suspend fun reconcileWithUsageStats() {
        withContext(Dispatchers.IO) {
            try {
                val usageStatsManager =
                    context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                        ?: return@withContext

                val zone = ZoneId.systemDefault()
                val today = LocalDate.now(zone)

                for (i in 0..6) {
                    val day = today.minusDays(i.toLong())
                    val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
                    val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
                    val dayKey = day.toString()

                    val stats = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        start,
                        end
                    )

                    stats?.forEach { stat ->
                        val packageName = stat.packageName ?: return@forEach
                        val total = stat.totalTimeInForeground
                        if (total <= 0) return@forEach

                        val existing = dao.getUsageForPackageDate(packageName, dayKey) ?: 0L
                        if (total > existing) {
                            val delta = total - existing
                            val updated = dao.addUsage(packageName, dayKey, delta)
                            if (updated == 0) {
                                dao.insert(UsageEntry(packageName, dayKey, total))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UsageRepository", "Reconcile failed: ${e.message}", e)
            }
        }
    }

    suspend fun getTodayUsage(): Long {
        val todayKey = LocalDate.now(ZoneId.systemDefault()).toString()
        return withContext(Dispatchers.IO) {
            val base = dao.getTotalForDate(todayKey) ?: 0L
            val live = getLiveSessionDelta()
            val stats = getUsageStatsTotalForToday()
            maxOf(base + live, stats)
        }
    }

    suspend fun getWeeklyUsage(): Long {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val weekStart = getWeekStartDate(today)
        val dates = generateSequence(weekStart) { it.plusDays(1) }
            .takeWhile { it <= today }
            .map { it.toString() }
            .toList()
        return withContext(Dispatchers.IO) {
            val base = dao.getTotalForDates(dates) ?: 0L
            val live = getLiveSessionDelta()
            val stats = getUsageStatsTotalForWeek()
            maxOf(base + live, stats)
        }
    }

    suspend fun getPerAppUsage(): Map<String, Long> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val weekStart = getWeekStartDate(today)
        val dates = generateSequence(weekStart) { it.plusDays(1) }
            .takeWhile { it <= today }
            .map { it.toString() }
            .toList()
        return withContext(Dispatchers.IO) {
            val base = dao.getPerAppForDates(dates).associate { it.packageName to it.totalMs }
            val live = getLiveSession()
            if (live != null) {
                val updated = base.toMutableMap()
                updated[live.packageName] = (updated[live.packageName] ?: 0L) + live.deltaMs
                updated
            } else {
                base
            }
        }
    }

    suspend fun validateAccuracy(): AccuracyReport {
        return withContext(Dispatchers.IO) {
            val todayDb = dao.getTotalForDate(LocalDate.now(ZoneId.systemDefault()).toString()) ?: 0L
            val todayStats = getUsageStatsTotalForToday()
            val delta = kotlin.math.abs(todayDb - todayStats)

            if (delta > ACCURACY_WARNING_THRESHOLD_MS) {
                Log.w(
                    "UsageRepository",
                    "Screen time accuracy warning: db=$todayDb stats=$todayStats delta=$delta ms"
                )
            } else {
                Log.d(
                    "UsageRepository",
                    "Screen time accuracy ok: db=$todayDb stats=$todayStats delta=$delta ms"
                )
            }

            AccuracyReport(todayDb, todayStats, delta)
        }
    }

    private fun getLiveSessionDelta(): Long {
        val live = getLiveSession()
        return live?.deltaMs ?: 0L
    }

    private fun getLiveSession(): LiveSession? {
        return try {
            val prefs = context.getSharedPreferences("usage_tracking_state", Context.MODE_PRIVATE)
            val isScreenOn = prefs.getBoolean("screen_on", true)
            if (!isScreenOn) return null

            val packageName = prefs.getString("last_package", null) ?: return null
            val start = prefs.getLong("last_timestamp", 0L)
            val now = System.currentTimeMillis()
            if (start <= 0L || now <= start) return null

            // Cap live session to today's start to ensure daily reset at 00:00
            val zone = ZoneId.systemDefault()
            val todayStart = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
            val effectiveStart = maxOf(start, todayStart)
            if (now <= effectiveStart) return null

            LiveSession(packageName, now - effectiveStart)
        } catch (e: Exception) {
            Log.w("UsageRepository", "Live session read failed: ${e.message}")
            null
        }
    }

    private fun getUsageStatsTotalForToday(): Long {
        return try {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            getUsageStatsTotal(start, end)
        } catch (e: Exception) {
            Log.w("UsageRepository", "UsageStats today failed: ${e.message}")
            0L
        }
    }

    private fun getUsageStatsTotalForWeek(): Long {
        return try {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val weekStart = getWeekStartDate(today)
            val start = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            getUsageStatsTotal(start, end)
        } catch (e: Exception) {
            Log.w("UsageRepository", "UsageStats week failed: ${e.message}")
            0L
        }
    }

    private fun getUsageStatsTotal(startMs: Long, endMs: Long): Long {
        return try {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                    ?: return 0L
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startMs,
                endMs
            )
            stats?.sumOf { it.totalTimeInForeground } ?: 0L
        } catch (e: Exception) {
            Log.w("UsageRepository", "UsageStats total failed: ${e.message}")
            0L
        }
    }

    private data class LiveSession(
        val packageName: String,
        val deltaMs: Long
    )

    data class AccuracyReport(
        val todayDbMs: Long,
        val todayUsageStatsMs: Long,
        val deltaMs: Long
    )

    companion object {
        private const val ACCURACY_WARNING_THRESHOLD_MS = 10 * 60 * 1000L

        @Volatile
        private var INSTANCE: UsageRepository? = null

        fun getInstance(context: Context): UsageRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UsageRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private fun getLastDates(days: Int): List<String> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        return (0 until days).map { today.minusDays(it.toLong()).toString() }
    }

    private fun getWeekStartDate(today: LocalDate): LocalDate {
        val prefs = context.getSharedPreferences("safetimeguard_settings", Context.MODE_PRIVATE)
        val weekStartPref = prefs.getString("week_start", "monday") ?: "monday"
        val daysToSubtract = if (weekStartPref == "sunday") {
            (today.dayOfWeek.value % DayOfWeek.SUNDAY.value).toLong()
        } else {
            (today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()
        }
        return today.minusDays(daysToSubtract)
    }
}
