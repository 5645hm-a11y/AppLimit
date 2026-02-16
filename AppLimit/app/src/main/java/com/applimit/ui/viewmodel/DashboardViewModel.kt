package com.applimit.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.applimit.data.BlockRule
import com.applimit.data.RuleStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DashboardViewModel(private val app: Application) : ViewModel() {
    val rules: Flow<List<BlockRule>> = flow {
        emit(RuleStorage.load(app))
    }
}

class DashboardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
