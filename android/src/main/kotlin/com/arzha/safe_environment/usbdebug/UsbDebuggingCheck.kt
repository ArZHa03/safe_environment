package com.arzha.safe_environment.usbdebug

import android.content.Context
import android.os.Build
import android.provider.Settings

class DevelopmentModeCheck {
    companion object{
        fun isUsbDebuggingEnabled(context: Context): Boolean {
            if(Integer.valueOf(Build.VERSION.SDK_INT) == 16) {
                return android.provider.Settings.Secure.getInt(context.getContentResolver(),
                        android.provider.Settings.Secure.ADB_ENABLED , 0) != 0;
            } else if (Integer.valueOf(Build.VERSION.SDK_INT) >= 17) {
                return android.provider.Settings.Secure.getInt(context.getContentResolver(),
                        android.provider.Settings.Global.ADB_ENABLED , 0) != 0;
            } else return false;
        }
    }
}