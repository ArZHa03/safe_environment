package com.arzha.safe_environment.realdevice

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
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
        private val KNOWN_FILES = listOf("/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props", "/mnt/shared")
        private val KNOWN_QEMU_DRIVERS = listOf("goldfish")
        
        private fun checkFiles(targets: List<String>): Boolean {
            for (pipe in targets) {
                val file = File(pipe)
                if (file.exists()) return true
            }
            return false
        }

        private fun checkEmulatorFiles(): Boolean {
            return checkFiles(GENY_FILES) || checkFiles(ANDY_FILES) || checkFiles(NOX_FILES) || checkFiles(X86_FILES) || checkFiles(PIPES) || checkFiles(KNOWN_FILES)
        }

        private fun checkEmulatorProperties(): Boolean {
            val props = listOf("init.svc.qemud", "init.svc.qemu-props", "qemu.hw.mainkeys", "qemu.sf.fake_camera", "qemu.sf.lcd_density", "ro.kernel.android.qemud", "ro.kernel.qemu.gles", "ro.serialno")
            for (prop in props) {
                val value = System.getProperty(prop)
                if (value != null && value.contains("qemu")) {
                    return true
                }
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
            return (Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86") ||
                    Build.MANUFACTURER.contains("Genymotion") ||
                    Build.MODEL.startsWith("sdk_") ||
                    Build.DEVICE.startsWith("emulator") ||
                    ("QC_Reference_Phone" == Build.BOARD && !"xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)) ||
                    (Build.HOST.startsWith("Build") && !Build.MANUFACTURER.equals("sony", ignoreCase = true)) ||
                    (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                    SystemProperties.get("ro.kernel.qemu") == "1" ||
                    Build.HARDWARE.contains("goldfish") ||
                    Build.HARDWARE.contains("ranchu") ||
                    Build.PRODUCT.contains("google_sdk") || 
                    Build.PRODUCT.contains("vbox86p") ||
                    Build.PRODUCT.toLowerCase().contains("nox") ||
                    Build.BOARD.toLowerCase().contains("nox") ||
                    Build.HARDWARE.toLowerCase().contains("nox") ||
                    Build.MODEL.toLowerCase().contains("droid4x") ||
                    Build.HARDWARE == "vbox86" ||
                    checkEmulatorFiles() ||
                    checkEmulatorProperties() ||
                    hasQEmuDrivers())
        }
    }
}
