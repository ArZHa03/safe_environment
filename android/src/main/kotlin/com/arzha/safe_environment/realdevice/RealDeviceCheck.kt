package com.arzha.safe_environment.realdevice

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class RealDeviceCheck {
    companion object {
        private val GENY_FILES = listOf("/dev/socket/genyd", "/dev/socket/baseband_genyd")
        private val PIPES = listOf("/dev/socket/qemud", "/dev/qemu_pipe")
        private val X86_FILES = listOf(
            "ueventd.android_x86.rc",
            "x86.prop",
            "ueventd.ttVM_x86.rc",
            "init.ttVM_x86.rc",
            "fstab.ttVM_x86",
            "fstab.vbox86",
            "init.vbox86.rc",
            "ueventd.vbox86.rc"
        )
        private val ANDY_FILES = listOf("fstab.andy", "ueventd.andy.rc")
        private val NOX_FILES = listOf("fstab.nox", "init.nox.rc", "ueventd.nox.rc")
        private val KNOWN_NUMBERS = listOf("15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564", "15555215566",
            "15555215568", "15555215570", "15555215572", "15555215574", "15555215576", "15555215578",
            "15555215580", "15555215582", "15555215584")
        private val KNOWN_DEVICE_IDS = listOf("000000000000000", "e21833235b6eef10", "012345678912345")
        private val KNOWN_IMSI_IDS = listOf("310260000000000")
        private val KNOWN_FILES = listOf("/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props")
        private val KNOWN_QEMU_DRIVERS = listOf("goldfish")
        
        private fun checkFiles(targets: List<String>): Boolean {
            for (pipe in targets) {
                val file = File(pipe)
                if (file.exists()) return true
            }
            return false
        }

        private fun checkEmulatorFiles(): Boolean {
            return checkFiles(GENY_FILES) || checkFiles(ANDY_FILES) || checkFiles(NOX_FILES) || checkFiles(X86_FILES) || checkFiles(PIPES)
        }

        private fun checkEmulatorProperties(): Boolean {
            val props = listOf(
                "init.svc.qemud", "init.svc.qemu-props", "qemu.hw.mainkeys", "qemu.sf.fake_camera",
                "qemu.sf.lcd_density", "ro.bootloader", "ro.bootmode", "ro.hardware", "ro.kernel.android.qemud"
            )
            for (prop in props) {
                val value = System.getProperty(prop)
                if (value != null && value.contains("qemu")) {
                    return true
                }
            }
            return false
        }

        private fun hasKnownPhoneNumber(context: Context): Boolean {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                val phoneNumber = telephonyManager.line1Number
                if (KNOWN_NUMBERS.contains(phoneNumber)) {
                    return true
                }
            } catch (exception: SecurityException) {
                Log.v("RealDeviceCheck", "Unable to get phone number: ${exception.message}")
            }
            return false
        }

        private fun hasKnownDeviceId(context: Context): Boolean {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                val deviceId = telephonyManager.deviceId
                if (KNOWN_DEVICE_IDS.contains(deviceId)) {
                    return true
                }
            } catch (exception: SecurityException) {
                Log.v("RealDeviceCheck", "Unable to get device ID: ${exception.message}")
            }
            return false
        }

        private fun hasKnownImsi(context: Context): Boolean {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                val imsi = telephonyManager.subscriberId
                if (KNOWN_IMSI_IDS.contains(imsi)) {
                    return true
                }
            } catch (exception: SecurityException) {
                Log.v("RealDeviceCheck", "Unable to get IMSI: ${exception.message}")
            }
            return false
        }

        private fun hasQEmuDrivers(): Boolean {
            for (driversFile in listOf(File("/proc/tty/drivers"), File("/proc/cpuinfo"))) {
                if (driversFile.exists() && driversFile.canRead()) {
                    val data = ByteArray(1024)
                    try {
                        val isStream = FileInputStream(driversFile)
                        isStream.read(data)
                        isStream.close()
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }

                    val driverData = String(data)
                    for (knownDriver in KNOWN_QEMU_DRIVERS) {
                        if (driverData.contains(knownDriver)) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        fun isRealDevice(context: Context): Boolean {
            return !(Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86") ||
                    Build.MANUFACTURER.contains("Genymotion") ||
                    Build.MODEL.startsWith("sdk_") ||
                    Build.DEVICE.startsWith("emulator") ||
                    checkEmulatorFiles() ||
                    checkEmulatorProperties() ||
                    hasKnownPhoneNumber(context) ||
                    hasKnownDeviceId(context) ||
                    hasKnownImsi(context) ||
                    hasQEmuDrivers())
        }
    }
}
