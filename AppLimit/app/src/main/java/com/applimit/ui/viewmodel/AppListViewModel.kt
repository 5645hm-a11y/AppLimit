package com.applimit.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppListViewModel(private val app: Application) : ViewModel() {

    private val _apps = MutableStateFlow<List<String>>(emptyList())
    val apps = _apps.asStateFlow()

    init {
        viewModelScope.launch {
            // Placeholder for loading app list
            _apps.value = listOf("com.example.app1", "com.example.app2")
        }
    }
}

class AppListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
