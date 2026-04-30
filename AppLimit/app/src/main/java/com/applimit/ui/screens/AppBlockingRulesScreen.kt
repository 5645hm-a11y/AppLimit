package com.applimit.ui.screens

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.applimit.LanguageManager
import com.applimit.ui.components.DayChipRow
import com.applimit.ui.components.SafeTimeGuardTopAppBar
import java.util.UUID

data class BlockingRule(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val appName: String,
    val blockedHours: List<IntRange> = emptyList(),
    val blockedDays: List<Int> = emptyList(),
    val startHour: Int = 0,
    val startMinute: Int = 0,
    val endHour: Int = 0,
    val endMinute: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockingRulesScreen(language: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var installedApps by remember { mutableStateOf(emptyList<ApplicationInfo>()) }
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var blockingRules by remember { mutableStateOf(emptyList<BlockingRule>()) }
    var showSystemApps by remember { mutableStateOf(false) }
    var showBlockedApps by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val allApps = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        installedApps = if (showSystemApps) {
            allApps.sortedBy { context.packageManager.getApplicationLabel(it).toString() }
        } else {
            allApps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                .sortedBy { context.packageManager.getApplicationLabel(it).toString() }
        }
        val storedRules = com.applimit.data.RuleStorage.load(context)
        blockingRules = storedRules.map { rule ->
            BlockingRule(
                id = rule.id,
                packageName = rule.packageName,
                appName = try {
                    context.packageManager.getApplicationLabel(
                        context.packageManager.getApplicationInfo(rule.packageName, 0)
                    ).toString()
                } catch (_: Exception) { rule.packageName },
                startHour = rule.startHour,
                startMinute = rule.startMinute,
                endHour = rule.endHour,
                endMinute = rule.endMinute,
                blockedDays = rule.days.toList(),
                blockedHours = emptyList()
            )
        }
    }

    if (selectedApp != null) {
        BlockTimeConfigurationScreen(
            selectedApp = selectedApp!!,
            blockingRules = blockingRules,
            context = context,
            language = language,
            onSaveRule = { rule ->
                blockingRules = blockingRules.filter { it.id != rule.id } + rule
                val allRules = com.applimit.data.RuleStorage.load(context).toMutableList()
                allRules.removeAll { it.id == rule.id }
                allRules.add(
                    com.applimit.data.BlockRule(
                        id = rule.id,
                        packageName = rule.packageName,
                        days = rule.blockedDays.toSet(),
                        startHour = rule.startHour,
                        startMinute = rule.startMinute,
                        endHour = rule.endHour,
                        endMinute = rule.endMinute
                    )
                )
                com.applimit.data.RuleStorage.save(context, allRules)
            },
            onDeleteRule = { ruleId ->
                blockingRules = blockingRules.filter { it.id != ruleId }
                val allRules = com.applimit.data.RuleStorage.load(context).toMutableList()
                allRules.removeAll { it.id == ruleId }
                com.applimit.data.RuleStorage.save(context, allRules)
            },
            onBack = { selectedApp = null }
        )
        return
    }

    Scaffold(
        topBar = {
            SafeTimeGuardTopAppBar(
                title = LanguageManager.getString("block_apps", language),
                onNavigateBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Tab selector: All Apps / Blocked Apps ───────────────────────
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = !showBlockedApps,
                    onClick = { showBlockedApps = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text(LanguageManager.getString("all_apps", language)) }
                SegmentedButton(
                    selected = showBlockedApps,
                    onClick = { showBlockedApps = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text(LanguageManager.getString("blocked_apps", language)) }
            }

            // ── System apps toggle ──────────────────────────────────────────
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.getString("show_system_apps", language),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = LanguageManager.getString("include_system_apps_desc", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = showSystemApps,
                        onCheckedChange = { checked ->
                            showSystemApps = checked
                            val allApps = context.packageManager
                                .getInstalledApplications(PackageManager.GET_META_DATA)
                            installedApps = if (checked) {
                                allApps.sortedBy { context.packageManager.getApplicationLabel(it).toString() }
                            } else {
                                allApps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                                    .sortedBy { context.packageManager.getApplicationLabel(it).toString() }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val blockedPackages = blockingRules.map { it.packageName }.toSet()
            val visibleApps = if (showBlockedApps) {
                installedApps.filter { it.packageName in blockedPackages }
            } else {
                installedApps.filter { it.packageName !in blockedPackages }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(visibleApps) { app ->
                    val appName = context.packageManager.getApplicationLabel(app).toString()
                    val isBlocked = blockingRules.any { it.packageName == app.packageName }
                    AppListItem(
                        app = app,
                        appName = appName,
                        isBlocked = isBlocked,
                        context = context,
                        onClick = { selectedApp = app.packageName }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppListItem(
    app: ApplicationInfo,
    appName: String,
    isBlocked: Boolean,
    context: Context,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isBlocked)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val appIcon = remember(app) {
                try { context.packageManager.getApplicationIcon(app) } catch (_: Exception) { null }
            }

            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.toBitmap().asImageBitmap(),
                        contentDescription = appName,
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = appName.firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isBlocked) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockTimeConfigurationScreen(
    selectedApp: String,
    blockingRules: List<BlockingRule>,
    context: Context,
    language: String,
    onSaveRule: (BlockingRule) -> Unit,
    onDeleteRule: (String) -> Unit,
    onBack: () -> Unit
) {
    val appName = try {
        context.packageManager.getApplicationLabel(
            context.packageManager.getApplicationInfo(selectedApp, 0)
        ).toString()
    } catch (_: Exception) { selectedApp }

    var selectedStartHour by remember { mutableStateOf(9) }
    var selectedStartMinute by remember { mutableStateOf(0) }
    var selectedEndHour by remember { mutableStateOf(22) }
    var selectedEndMinute by remember { mutableStateOf(0) }
    var selectedDays by remember { mutableStateOf(setOf(0, 1, 2, 3, 4)) }
    var editingRuleId by remember { mutableStateOf<String?>(null) }

    val dayNames = listOf(
        LanguageManager.getString("mon", language),
        LanguageManager.getString("tue", language),
        LanguageManager.getString("wed", language),
        LanguageManager.getString("thu", language),
        LanguageManager.getString("fri", language),
        LanguageManager.getString("sat", language),
        LanguageManager.getString("sun", language)
    )

    val existingRules = blockingRules.filter { it.packageName == selectedApp }

    fun applyRuleToPicker(rule: BlockingRule) {
        editingRuleId = rule.id
        selectedStartHour = rule.startHour
        selectedStartMinute = rule.startMinute
        selectedEndHour = rule.endHour
        selectedEndMinute = rule.endMinute
        selectedDays = rule.blockedDays.toSet()
    }

    fun formatDays(days: List<Int>): String {
        if (days.isEmpty()) return ""
        return days.sorted().joinToString(", ") { dayNames.getOrNull(it) ?: "" }
    }

    Scaffold(
        topBar = {
            SafeTimeGuardTopAppBar(
                title = appName,
                onNavigateBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Time range ──────────────────────────────────────────────────
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = LanguageManager.getString("block_from", language),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimePickerButton(
                            hour = selectedStartHour,
                            minute = selectedStartMinute,
                            onTimeChange = { h, m -> selectedStartHour = h; selectedStartMinute = m },
                            modifier = Modifier.weight(1f),
                            language = language
                        )
                        Text(
                            text = LanguageManager.getString("to", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TimePickerButton(
                            hour = selectedEndHour,
                            minute = selectedEndMinute,
                            onTimeChange = { h, m -> selectedEndHour = h; selectedEndMinute = m },
                            modifier = Modifier.weight(1f),
                            language = language
                        )
                    }
                }
            }

            // ── Day selection ───────────────────────────────────────────────
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = LanguageManager.getString("block_on_days", language),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    DayChipRow(
                        days = dayNames,
                        selectedDays = selectedDays,
                        onDayToggled = { index ->
                            selectedDays = if (index in selectedDays) selectedDays - index else selectedDays + index
                        }
                    )
                }
            }

            // ── Existing schedules for this app ─────────────────────────────
            if (existingRules.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = LanguageManager.getString("blocked_until", language),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        existingRules.forEach { rule ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { applyRuleToPicker(rule) },
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (editingRuleId == rule.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = String.format(
                                                "%02d:%02d – %02d:%02d",
                                                rule.startHour, rule.startMinute,
                                                rule.endHour, rule.endMinute
                                            ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        val daysText = formatDays(rule.blockedDays)
                                        if (daysText.isNotBlank()) {
                                            Text(
                                                text = daysText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onDeleteRule(rule.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Action buttons ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(LanguageManager.getString("cancel", language))
                }
                Button(
                    onClick = {
                        val rule = BlockingRule(
                            id = editingRuleId ?: UUID.randomUUID().toString(),
                            packageName = selectedApp,
                            appName = appName,
                            blockedHours = listOf(selectedStartHour..selectedEndHour),
                            blockedDays = selectedDays.toList(),
                            startHour = selectedStartHour,
                            startMinute = selectedStartMinute,
                            endHour = selectedEndHour,
                            endMinute = selectedEndMinute
                        )
                        onSaveRule(rule)
                        editingRuleId = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        text = LanguageManager.getString("save_rule", language),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerButton(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempHour by remember(hour) { mutableStateOf(hour) }
    var tempMinute by remember(minute) { mutableStateOf(minute) }
    var hourText by remember(hour) { mutableStateOf(String.format("%02d", hour)) }
    var minuteText by remember(minute) { mutableStateOf(String.format("%02d", minute)) }

    ElevatedCard(
        modifier = modifier,
        onClick = { showDialog = true },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d:%02d", hour, minute),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = {
                Text(
                    text = String.format("%02d:%02d", tempHour, tempMinute),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = LanguageManager.getString("hour", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = hourText,
                            onValueChange = { v ->
                                if (v.length <= 2 && v.all { it.isDigit() }) {
                                    hourText = v
                                    v.toIntOrNull()?.let { h -> if (h in 0..23) tempHour = h }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = LanguageManager.getString("minute", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { v ->
                                if (v.length <= 2 && v.all { it.isDigit() }) {
                                    minuteText = v
                                    v.toIntOrNull()?.let { m -> if (m in 0..59) tempMinute = m }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTimeChange(tempHour, tempMinute)
                        showDialog = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(LanguageManager.getString("save_rule", language)) }
            }
        )
    }
}

fun Drawable.toBitmap(): android.graphics.Bitmap {
    val width = intrinsicWidth.takeIf { it > 0 } ?: 1
    val height = intrinsicHeight.takeIf { it > 0 } ?: 1
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
