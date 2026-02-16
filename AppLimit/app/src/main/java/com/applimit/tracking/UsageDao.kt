package com.applimit.tracking

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsageDao {
    @Query(
        "UPDATE usage_entries SET totalForegroundTimeMs = totalForegroundTimeMs + :delta " +
            "WHERE packageName = :packageName AND date = :date"
    )
    fun addUsage(packageName: String, date: String, delta: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: UsageEntry)

    @Query(
        "SELECT totalForegroundTimeMs FROM usage_entries " +
            "WHERE packageName = :packageName AND date = :date LIMIT 1"
    )
    fun getUsageForPackageDate(packageName: String, date: String): Long?

    @Query("SELECT SUM(totalForegroundTimeMs) FROM usage_entries WHERE date = :date")
    fun getTotalForDate(date: String): Long?

    @Query("SELECT SUM(totalForegroundTimeMs) FROM usage_entries WHERE date IN (:dates)")
    fun getTotalForDates(dates: List<String>): Long?

    @Query(
        "SELECT packageName, SUM(totalForegroundTimeMs) AS totalMs " +
            "FROM usage_entries WHERE date IN (:dates) GROUP BY packageName"
    )
    fun getPerAppForDates(dates: List<String>): List<UsagePerApp>
}

data class UsagePerApp(
    @ColumnInfo(name = "packageName") val packageName: String,
    @ColumnInfo(name = "totalMs") val totalMs: Long
)
