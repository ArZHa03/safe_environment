import 'package:flutter/services.dart';

class SafeEnvironment {
  static const _channel = MethodChannel('safe_environment');

  static Future<bool> get isRootedDevice async {
    final bool isRootedDevice = await _channel.invokeMethod('isRootedDevice');
    return isRootedDevice;
  }

  static Future<bool> get isRealDevice async {
    final bool isRealDevice = await _channel.invokeMethod('isRealDevice');
    return isRealDevice;
  }

  static Future<bool> get isExternalStorage async {
    final bool isExternalStorage = await _channel.invokeMethod('isExternalStorage');
    return isExternalStorage;
  }

  static Future<bool> get isUsbDebuggingEnabled async {
    final bool isUsbDebuggingEnabled = await _channel.invokeMethod('isUsbDebuggingEnabled');
    return isUsbDebuggingEnabled;
  }
}
