package com.arzha.safe_environment.rooted

import android.content.Context
import android.os.Build
import android.content.pm.PackageManager
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Scanner
import java.util.Locale
import com.scottyab.rootbeer.RootBeer

class RootedDeviceCheck {
    companion object {
        private val rootsAppPackage = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk"
        )

        private val dangerousListApps = arrayOf(
            "com.koushikdutta.rommanager",
            "com.koushikdutta.rommanager.license",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine",
            "com.ramdroid.appquarantinepro",
            "com.android.vending.billing.InAppBillingService.COIN",
            "com.chelpus.luckypatcher"
        )

        private val rootCloakingApps = arrayOf(
            "com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.zachspong.temprootremovejb",
            "com.amphoras.hidemyroot",
            "com.amphoras.hidemyrootadfree",
            "com.formyhm.hiderootPremium",
            "com.formyhm.hideroot"
        )

        private val superUserPath = arrayOf(
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su",
            "/sbin/su",
            "/su/bin/su",
            "/system/bin/su",
            "/system/bin/.ext/su",
            "/system/bin/failsafe/su",
            "/system/sd/xbin/su",
            "/system/usr/we-need-root/su",
            "/system/xbin/su",
            "/cache/su",
            "/data/su",
            "/dev/su",
            "/data/adb/magisk",
            "/data/adb/.magisk",
            "/data/adb/magisk/busybox",
            "/data/adb/.magisk/busybox",
            "/system/xbin/daemonsu",
            "/data/adb/magisk.img",
            "/data/adb/magisk",
            "/data/adb/.magisk"
        )

        private val notWritablePath = arrayOf(
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            "/sbin",
            "/etc"
        )

        private const val ONEPLUS = "oneplus"
        private const val MOTO = "moto"
        private const val XIAOMI = "xiaomi"
        private const val LENOVO = "lenovo"

        private fun isHaveReadWritePermission(): Boolean {
            var result = false
            val lines = commander("mount")
            if (lines.isNullOrEmpty()) return result
            for (line in lines) {
                val args = line.split(" ")
                if (args.size < 4) continue
                val mountPoint = args[1]
                val mountOptions = args[3]
                for (path in notWritablePath) {
                    if (mountPoint.equals(path, ignoreCase = true)) {
                        for (opt in mountOptions.split(",")) {
                            if (opt.equals("rw", ignoreCase = true)) {
                                return true
                            }
                        }
                    }
                }
            }
            return result
        }

        private fun isSUExist(): Boolean {
            return try {
                val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                reader.readLine() != null
            } catch (e: Exception) {
                false
            }
        }

        private fun isTestBuildKey(): Boolean {
            val buildTags = Build.TAGS
            return buildTags != null && buildTags.contains("test-keys")
        }

        private fun isPathExist(ext: String): Boolean {
            superUserPath.forEach { path ->
                if (File(path).exists()) {
                    return true
                }
            }
            return false
        }

        private fun isHaveDangerousProperties(): Boolean {
            val dangerousProps = mapOf(
                "ro.debuggable" to "1",
                "ro.secure" to "0"
            )
            val lines = commander("getprop")
            if (lines == null) return false

            lines.forEach { line ->
                dangerousProps.forEach { (key, badValue) ->
                    if (line.contains(key) && line.contains("[$badValue]")) {
                        return true
                    }
                }
            }
            return false
        }

        private fun isHaveDangerousApps(): Boolean {
            val packages = dangerousListApps.toList()
            return isAnyPackageFromListInstalled(packages)
        }

        private fun isHaveRootManagementApps(): Boolean {
            val packages = rootsAppPackage.toList()
            return isAnyPackageFromListInstalled(packages)
        }

        private fun isAnyPackageFromListInstalled(pkg: List<String>): Boolean {
            val pm = applicationContext.packageManager
            return pkg.any { packageName ->
                try {
                    pm.getPackageInfo(packageName, 0)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        private fun commander(command: String): Array<String>? {
            return try {
                val process = Runtime.getRuntime().exec(command)
                val inputStream = process.inputStream
                val result = Scanner(inputStream).useDelimiter("\\A").next()
                result.split("\n").toTypedArray()
            } catch (e: Exception) {
                null
            }
        }

        fun isRootedDevice(context: Context): Boolean {
            val check: CheckApiVersion = if (Build.VERSION.SDK_INT >= 23) {
                GreaterThan23()
            } else {
                LessThan23()
            }
            return check.checkRootedDevice() || rootBeerCheck(context) || isHaveReadWritePermission() || isPathExist("su") || isSUExist() || isTestBuildKey() || isHaveDangerousApps() || isHaveRootManagementApps() || isHaveDangerousProperties()
        }

        private fun rootBeerCheck(context: Context): Boolean {
            val rootBeer = RootBeer(context)
            val brand = Build.BRAND.lowercase(Locale.getDefault())
            return if (brand.contains(ONEPLUS) || brand.contains(MOTO) || brand.contains(XIAOMI) || brand.contains(LENOVO)) {
                rootBeer.isRootedWithBusyBoxCheck
            } else {
                rootBeer.isRooted
            }
        }
    }
}
