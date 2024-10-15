package com.arzha.safe_environment.realdevice

import android.os.Build

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RealDeviceCheck {
    
    private static ArrayList<String> GENY_FILES = new ArrayList<>(
        Arrays.asList(
            "/dev/socket/genyd",
            "/dev/socket/baseband_genyd"
        )
    );

    private static ArrayList<String> PIPES = new ArrayList<>(
        Arrays.asList(
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
        )
    );

    private static ArrayList<String> X86_FILES = new ArrayList<>(
        Arrays.asList(
            "ueventd.android_x86.rc",
            "x86.prop",
            "ueventd.ttVM_x86.rc",
            "init.ttVM_x86.rc",
            "fstab.ttVM_x86",
            "fstab.vbox86",
            "init.vbox86.rc",
            "ueventd.vbox86.rc"
        )
    );

    private static ArrayList<String> ANDY_FILES = new ArrayList<>(
        Arrays.asList(
            "fstab.andy",
            "ueventd.andy.rc"
        )
    );

    private static ArrayList<String> NOX_FILES = new ArrayList<>(
        Arrays.asList(
            "fstab.nox",
            "init.nox.rc",
            "ueventd.nox.rc"
        )
    );

    private static boolean checkFiles(List<String> targets){
        for(String pipe : targets){
            File file = new File(pipe);
            if (file.exists())
                return true;
        }
        return false;
    }

    private static boolean checkEmulatorFiles (){
        return (checkFiles(GENY_FILES)
                || checkFiles(ANDY_FILES)
                || checkFiles(NOX_FILES)
                || checkFiles(X86_FILES)
                || checkFiles(PIPES));
    }
    
    companion object{
        fun isRealDevice(): Boolean {
            return (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.MODEL.startsWith("sdk_")
                    || Build.DEVICE.startsWith("emulator"))
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || "google_sdk" == Build.PRODUCT
                    || "QC_Reference_Phone" == Build.BOARD && !"xiaomi".equalsIgnoreCase(Build.MANUFACTURER)
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.HOST.startsWith("Build") && !Build.MANUFACTURER.equalsIgnoreCase("sony"))
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || Build.PRODUCT == "google_sdk"
                    || SystemProperties.get("ro.kernel.qemu") == "1"
                    || Build.HARDWARE.contains("goldfish")
                    || Build.HARDWARE.contains("ranchu")
                    || Build.PRODUCT.contains("vbox86p")
                    || Build.PRODUCT.toLowerCase().contains("nox")
                    || Build.BOARD.toLowerCase().contains("nox")
                    || Build.HARDWARE.toLowerCase().contains("nox")
                    || Build.MODEL.toLowerCase().contains("droid4x")
                    || Build.HARDWARE == "vbox86"
                    || checkEmulatorFiles()
        }
    }
}