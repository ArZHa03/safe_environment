# safe_environment

A plugin to check the integrity of the device.

## Features

- Check if the device is rooted
- Check if the device is a real device
- Check if external storage is available
- Check if USB debugging is enabled

## Installation

Add this to your package's `pubspec.yaml` file and then run `pub get`:

```yaml
dependencies:
  safe_environment: 
    git: https://github.com/ArZHa03/safe_environment.git
```

## Usage

Import the package and use the provided methods:

```dart
import 'package:safe_environment/safe_environment.dart';
```

```dart
void checkDeviceIntegrity() async {
  bool isRooted = await SafeEnvironment.isRootedDevice;
  bool isReal = await SafeEnvironment.isRealDevice;
  bool hasExternalStorage = await SafeEnvironment.isExternalStorage;
  bool isUsbDebugging = await SafeEnvironment.isUsbDebuggingEnabled;

  print('Is Rooted: $isRooted');
  print('Is Real Device: $isReal');
  print('Has External Storage: $hasExternalStorage');
  print('Is USB Debugging Enabled: $isUsbDebugging');
}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.