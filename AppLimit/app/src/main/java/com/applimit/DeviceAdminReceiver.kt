package com.applimit

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class DeviceAdminReceiver : DeviceAdminReceiver() {
    
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        // Prevent disabling without going through the app with PIN verification
        // This message will be shown to the user when they try to remove the device admin
        return context.getString(R.string.device_admin_disable_warning)
    }
    
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "AppLimit protection disabled", Toast.LENGTH_SHORT).show()
    }
    
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "AppLimit protection enabled", Toast.LENGTH_SHORT).show()
    }
}
