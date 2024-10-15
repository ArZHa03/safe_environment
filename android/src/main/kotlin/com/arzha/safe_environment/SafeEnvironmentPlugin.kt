package com.arzha.safe_environment


import android.content.Context

import com.arzha.safe_environment.externalstorage.ExternalStorageCheck
import com.arzha.safe_environment.realdevice.RealDeviceCheck
import com.arzha.safe_environment.rooted.RootedDeviceCheck
import com.arzha.safe_environment.usbdebugging.UsbDebuggingCheck

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** SafeEnvironmentPlugin */
class SafeEnvironmentPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel : MethodChannel
  private var context: Context? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext()
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "safe_environment")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") return result.success("Android ${Build.VERSION.RELEASE}")
    if (call.method.equals("isRootedDevice")) return result.success(context?.let { RootedDeviceCheck.isRootedDevice(it) })
    if (call.method.equals("isRealDevice")) return result.success(!RealDeviceCheck.isRealDevice())
    if (call.method.equals("isExternalStorage")) return result.success(context?.let { ExternalStorageCheck.isExternalStorage(it) })
    if (call.method.equals("isUsbDebuggingEnabled")) return result.success(UsbDebuggingCheck.isUsbDebuggingEnabled(context))
    return result.notImplemented()
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
