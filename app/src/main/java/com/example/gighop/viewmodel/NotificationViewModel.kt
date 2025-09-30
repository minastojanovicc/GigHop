package com.example.gighop.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.edit


class NotificationViewModel(val context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val _serviceRunningState = MutableLiveData<Boolean>().apply {
        value = sharedPreferences.getBoolean("is_notification_enabled", false)
    }
    val serviceRunningState: LiveData<Boolean> = _serviceRunningState

    fun setNotificationEnabled(enabled: Boolean) {
        _serviceRunningState.value = enabled
        sharedPreferences.edit { putBoolean("is_notification_enabled", enabled) }
    }

    private val _distanceNotification = MutableLiveData<Float>().apply {
        value = sharedPreferences.getFloat("distance_notification", 100f)
    }
    val distanceNotification: LiveData<Float> = _distanceNotification

    fun setDistanceNotification(distance: Float) {
        _distanceNotification.value = distance
        sharedPreferences.edit { putFloat("distance_notification", distance) }
    }
}

class NotificationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
