package com.applimit.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.applimit.data.BlockRule
import com.applimit.data.RuleStorage
import kotlinx.coroutines.launch

class ScheduleViewModel(private val app: Application) : ViewModel() {

    fun saveRule(
        packageName: String,
        days: Set<Int>,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) {
        viewModelScope.launch {
            val rules = RuleStorage.load(app).toMutableList()
            rules.removeAll { it.packageName == packageName }
            rules.add(
                BlockRule(
                    packageName = packageName,
                    days = days,
                    startHour = startHour,
                    startMinute = startMinute,
                    endHour = endHour,
                    endMinute = endMinute
                )
            )
            RuleStorage.save(app, rules)
        }
    }
}

class ScheduleViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
