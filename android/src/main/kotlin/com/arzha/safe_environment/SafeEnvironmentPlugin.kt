package com.arzha.safe_environment

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import com.arzha.safe_environment.externalstorage.ExternalStorageCheck
import com.arzha.safe_environment.realdevice.RealDeviceCheck
import com.arzha.safe_environment.rooted.RootedDeviceCheck
import com.arzha.safe_environment.usbdebug.UsbDebuggingCheck

class SafeEnvironmentPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private var context: Context? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "safe_environment")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "isRootedDevice" -> {
        context?.let { 
          result.success(RootedDeviceCheck.isRootedDevice(it)) 
        } ?: run {
          result.error("UNAVAILABLE", "Context is not available", null)
        }
      }
      "isRealDevice" -> {
        context?.let {
          result.success(!RealDeviceCheck.isRealDevice(it))
        } ?: run {
          result.error("UNAVAILABLE", "Context is not available", null)
        }
      }
      "isExternalStorage" -> {
        context?.let { 
          result.success(ExternalStorageCheck.isExternalStorage(it)) 
        } ?: run {
          result.error("UNAVAILABLE", "Context is not available", null)
        }
      }
      "isUsbDebuggingEnabled" -> {
        context?.let { 
          result.success(UsbDebuggingCheck.isUsbDebuggingEnabled(it)) 
        } ?: run {
          result.error("UNAVAILABLE", "Context is not available", null)
        }
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
