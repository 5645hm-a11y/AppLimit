package com.applimit.ui.screens

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applimit.LanguageManager
import com.applimit.ui.theme.Shapes
import java.util.UUID
import kotlinx.coroutines.launch

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

@Composable
fun AppBlockingRulesScreen(language: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var installedApps by remember { mutableStateOf(emptyList<ApplicationInfo>()) }
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var blockingRules by remember { mutableStateOf(emptyList<BlockingRule>()) }
    var showSystemApps by remember { mutableStateOf(false) }
    var showBlockedApps by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Load rules from storage when screen opens
    LaunchedEffect(Unit) {
        val allApps = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        installedApps = if (showSystemApps) {
            allApps.sortedBy { context.packageManager.getApplicationLabel(it).toString() }
        } else {
            allApps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                    .sortedBy { context.packageManager.getApplicationLabel(it).toString() }
        }
        
        // Load existing rules from storage
        val storedRules = com.applimit.data.RuleStorage.load(context)
        blockingRules = storedRules.map { rule ->
            BlockingRule(
                id = rule.id,
                packageName = rule.packageName,
                appName = try {
                    context.packageManager.getApplicationLabel(
                        context.packageManager.getApplicationInfo(rule.packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    rule.packageName
                },
                startHour = rule.startHour,
                startMinute = rule.startMinute,
                endHour = rule.endHour,
                endMinute = rule.endMinute,
                blockedDays = rule.days.toList(),
                blockedHours = emptyList()
            )
        }
    }

    val title = LanguageManager.getString("block_apps", language)
    val allAppsLabel = LanguageManager.getString("all_apps", language)
    val blockedAppsLabel = LanguageManager.getString("blocked_apps", language)

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(
                        modifier = Modifier
                                .fillMaxHeight()
                                .width(280.dp),
                        color = Color(0xFF1A1F2A)
                ) {
                    Column(
                            modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                                text = LanguageManager.getString("block_apps", language),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF),
                                modifier = Modifier.padding(vertical = 16.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )

                        Surface(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showBlockedApps = false
                                            scope.launch { drawerState.close() }
                                        },
                                shape = RoundedCornerShape(8.dp),
                                color = if (!showBlockedApps) Color(0xFF00D4AA).copy(alpha = 0.2f) else Color.Transparent
                        ) {
                            Row(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                        text = allAppsLabel,
                                        fontSize = 16.sp,
                                        fontWeight = if (!showBlockedApps) FontWeight.Bold else FontWeight.Normal,
                                        color = if (!showBlockedApps) Color(0xFF00D4AA) else Color(0xFFFFFFFF)
                                )
                            }
                        }

                        Surface(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showBlockedApps = true
                                            scope.launch { drawerState.close() }
                                        },
                                shape = RoundedCornerShape(8.dp),
                                color = if (showBlockedApps) Color(0xFF00D4AA).copy(alpha = 0.2f) else Color.Transparent
                        ) {
                            Row(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                        text = blockedAppsLabel,
                                        fontSize = 16.sp,
                                        fontWeight = if (showBlockedApps) FontWeight.Bold else FontWeight.Normal,
                                        color = if (showBlockedApps) Color(0xFF00D4AA) else Color(0xFFFFFFFF)
                                )
                            }
                        }
                    }
                }
            }
    ) {
    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(
                            Brush.verticalGradient(
                                    colors = listOf(
                                            Color(0xFF0F1419),
                                            Color(0xFF1A2A3F)
                                    )
                            )
                    )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern Header
            Surface(
                    modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                    Brush.verticalGradient(
                                            colors = listOf(
                                                    Color(0xFF00B4DB).copy(alpha = 0.1f),
                                                    Color(0xFF00D4AA).copy(alpha = 0.08f)
                                            )
                                    )
                            ),
                    color = Color.Transparent,
                    shadowElevation = 0.dp
            ) {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                            color = Color(0xFF00D4AA).copy(alpha = 0.15f),
                                            shape = CircleShape
                                    )
                    ) {
                        Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color(0xFF00D4AA),
                                modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                            color = Color(0xFF00D4AA).copy(alpha = 0.15f),
                                            shape = CircleShape
                                    )
                    ) {
                        Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF00D4AA),
                                modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                        )
                        Text(
                                text = if (showBlockedApps) blockedAppsLabel else allAppsLabel,
                                fontSize = 12.sp,
                                color = Color(0xFF22D3EE)
                        )
                    }
                }
            }

            if (selectedApp == null) {
                // Apps List
                Column(modifier = Modifier.fillMaxSize()) {
                    // System Apps Toggle
                    Surface(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1A1F2A),
                            shadowElevation = 2.dp
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
                                        text = LanguageManager.getString("show_system_apps", language),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                )
                                Text(
                                        text = LanguageManager.getString("include_system_apps_desc", language),
                                        fontSize = 11.sp,
                                        color = Color(0xFF22D3EE)
                                )
                            }
                            Switch(
                                    checked = showSystemApps,
                                    onCheckedChange = { checked ->
                                        showSystemApps = checked
                                        val allApps = context.packageManager
                                                .getInstalledApplications(PackageManager.GET_META_DATA)
                                        installedApps = if (checked) {
                                            allApps.sortedBy { context.packageManager
                                                    .getApplicationLabel(it).toString() }
                                        } else {
                                            allApps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                                                    .sortedBy { context.packageManager
                                                            .getApplicationLabel(it).toString() }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFF00D4AA),
                                            checkedTrackColor = Color(0xFF00D4AA).copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    val blockedPackages = blockingRules.map { it.packageName }.toSet()
                    val visibleApps = if (showBlockedApps) {
                        installedApps.filter { it.packageName in blockedPackages }
                    } else {
                        installedApps.filter { it.packageName !in blockedPackages }
                    }

                    // Apps List
                    LazyColumn(
                            modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(visibleApps) { app ->
                            val appName = context.packageManager
                                    .getApplicationLabel(app).toString()
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
            } else {
                // Block time configuration
                BlockTimeConfigurationScreen(
                        selectedApp = selectedApp!!,
                        blockingRules = blockingRules,
                        context = context,
                        language = language,
                        onSaveRule = { rule ->
                            // Update UI state (allow multiple rules per app)
                            blockingRules = blockingRules.filter { it.id != rule.id } + rule
                            
                            // Save to storage (do not overwrite by package name)
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
                            // Update UI state
                            blockingRules = blockingRules
                                    .filter { it.id != ruleId }
                            
                            // Delete from storage
                            val allRules = com.applimit.data.RuleStorage.load(context).toMutableList()
                            allRules.removeAll { it.id == ruleId }
                            com.applimit.data.RuleStorage.save(context, allRules)
                        },
                        onBack = { selectedApp = null }
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
    Surface(
            modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onClick),
            color = Color(0xFF1A1F2A),
            shadowElevation = 2.dp
    ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Icon
            val appIcon = remember(app) {
                try {
                    context.packageManager.getApplicationIcon(app)
                } catch (e: Exception) {
                    null
                }
            }

            Box(
                    modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00B4DB).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
            ) {
                if (appIcon != null) {
                    Image(
                            bitmap = appIcon.toBitmap().asImageBitmap(),
                            contentDescription = appName,
                            modifier = Modifier.size(44.dp),
                            contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                            text = appName.firstOrNull()?.toString() ?: "?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D4AA)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = appName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFFFFFFF),
                        maxLines = 1
                )
                Text(
                        text = app.packageName,
                        fontSize = 11.sp,
                        color = Color(0xFF22D3EE),
                        maxLines = 1
                )
            }

            if (isBlocked) {
                Surface(
                        modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                        color = Color(0xFF00D4AA).copy(alpha = 0.2f),
                        shadowElevation = 1.dp
                ) {
                    Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Blocked",
                            tint = Color(0xFF00D4AA),
                            modifier = Modifier
                                    .size(32.dp)
                                    .padding(6.dp)
                    )
                }
            }
        }
    }
}

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
    val appName = context.packageManager
            .getApplicationLabel(context.packageManager.getApplicationInfo(selectedApp, 0))
            .toString()

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
        return days.sorted().joinToString(", ") { dayIndex ->
            dayNames.getOrNull(dayIndex) ?: ""
        }
    }

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(
                            Brush.verticalGradient(
                                    colors = listOf(
                                            Color(0xFF0F1419),
                                            Color(0xFF1A2A3F)
                                    )
                            )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button
        IconButton(
                onClick = onBack,
                modifier = Modifier
                        .size(40.dp)
                        .background(
                                color = Color(0xFF00D4AA).copy(alpha = 0.15f),
                                shape = CircleShape
                        )
        ) {
            Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF00D4AA),
                    modifier = Modifier.size(20.dp)
            )
        }

        // App name
        Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2A),
                shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        LanguageManager.getString("app_to_block", language),
                        fontSize = 12.sp,
                        color = Color(0xFF22D3EE)
                )
                Text(
                        appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Time selection
        Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2A),
                shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        LanguageManager.getString("block_from", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerButton(
                            hour = selectedStartHour,
                            minute = selectedStartMinute,
                            onTimeChange = { h, m ->
                                selectedStartHour = h
                                selectedStartMinute = m
                            },
                            modifier = Modifier.weight(1f)
                    )
                    Text(
                            LanguageManager.getString("to", language),
                            fontSize = 12.sp,
                            color = Color(0xFF22D3EE)
                    )
                    TimePickerButton(
                            hour = selectedEndHour,
                            minute = selectedEndMinute,
                            onTimeChange = { h, m ->
                                selectedEndHour = h
                                selectedEndMinute = m
                            },
                            modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Day selection
        Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2A),
                shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        LanguageManager.getString("block_on_days", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dayNames.forEachIndexed { index, day ->
                        val isSelected = index in selectedDays
                        Surface(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedDays = if (isSelected) {
                                                selectedDays - index
                                            } else {
                                                selectedDays + index
                                            }
                                        }
                                        .background(
                                                if (isSelected)
                                                    Color(0xFF00D4AA).copy(alpha = 0.2f)
                                                else
                                                    Color(0xFF2A3240)
                                        ),
                                color = Color.Transparent,
                                shadowElevation = 0.dp
                        ) {
                            Row(
                                    modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                        text = day,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected)
                                            Color(0xFF00D4AA)
                                        else
                                            Color(0xFFFFFFFF)
                                )
                                if (isSelected) {
                                    Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF00D4AA),
                                            modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Existing rules list (allow multiple schedules per app)
        if (existingRules.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2A),
                shadowElevation = 2.dp
            ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    LanguageManager.getString("blocked_until", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                )
                existingRules.forEach { rule ->
                Surface(
                    modifier = Modifier
                            .fillMaxWidth()
                            .clickable { applyRuleToPicker(rule) },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF2A3240)
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
                            text =
                                String.format(
                                    "%02d:%02d - %02d:%02d",
                                    rule.startHour,
                                    rule.startMinute,
                                    rule.endHour,
                                    rule.endMinute
                                ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D4AA)
                        )
                        val daysText = formatDays(rule.blockedDays)
                        if (daysText.isNotBlank()) {
                        Text(
                            text = daysText,
                            fontSize = 12.sp,
                            color = Color(0xFFB0BEC5),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        }
                    }
                    IconButton(onClick = { onDeleteRule(rule.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF6B6B)
                        )
                    }
                    }
                }
                }
            }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Action buttons
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
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF22D3EE)
                    )
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
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D4AA)
                    )
            ) {
                Text(
                    text = LanguageManager.getString("save_rule", language),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Deletions are handled per-rule above
    }
}

@Composable
private fun TimePickerButton(
        hour: Int,
        minute: Int,
        onTimeChange: (Int, Int) -> Unit,
        modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempHour by remember(hour) { mutableStateOf(hour) }
    var tempMinute by remember(minute) { mutableStateOf(minute) }
    var hourText by remember(hour) { mutableStateOf(String.format("%02d", hour)) }
    var minuteText by remember(minute) { mutableStateOf(String.format("%02d", minute)) }
    var language = LanguageManager.ENGLISH  // Default language

    Column(modifier = modifier) {
        Surface(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(Shapes.medium)
                        .clickable { showDialog = true },
                color = Color(0xFF2A3240),
                shadowElevation = 2.dp
        ) {
            Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        "${String.format("%02d:%02d", hour, minute)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00D4AA)
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF1A2A3F),
            title = {
                Text(
                    "${String.format("%02d:%02d", tempHour, tempMinute)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D4AA),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
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
                                LanguageManager.getString("hour", language),
                                fontSize = 12.sp,
                                color = Color(0xFFFFFFFF).copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = hourText,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        hourText = newValue
                                        newValue.toIntOrNull()?.let { h ->
                                            if (h in 0..23) tempHour = h
                                        }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00D4AA),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF00D4AA),
                                    unfocusedTextColor = Color(0xFF00D4AA),
                                    focusedBorderColor = Color(0xFF00D4AA),
                                    unfocusedBorderColor = Color(0xFF00D4AA).copy(alpha = 0.5f),
                                    cursorColor = Color(0xFF00D4AA)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Text(
                            ":",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D4AA)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                LanguageManager.getString("minute", language),
                                fontSize = 12.sp,
                                color = Color(0xFFFFFFFF).copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = minuteText,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        minuteText = newValue
                                        newValue.toIntOrNull()?.let { m ->
                                            if (m in 0..59) tempMinute = m
                                        }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00D4AA),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF00D4AA),
                                    unfocusedTextColor = Color(0xFF00D4AA),
                                    focusedBorderColor = Color(0xFF00D4AA),
                                    unfocusedBorderColor = Color(0xFF00D4AA).copy(alpha = 0.5f),
                                    cursorColor = Color(0xFF00D4AA)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTimeChange(tempHour, tempMinute)
                        showDialog = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D4AA)
                    )
                ) { Text("Set") }
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
