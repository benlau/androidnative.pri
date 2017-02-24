Android Integration with Qt
===========================

It is forked from the QuickAndroid project that aim to provide a library to access Android functions from Qt/QML without using JNI.

Features
========

 1. Messege queue (SystemDispatcher) between C++/Qt and Java/Android code
  1. Auto conversion between C++ and Java data type. No need to write in JNI.
  1. It could be used to write your own Java code
 1. Image Picker
 1. Toast
 1. Wrapper of 	android.os.Environment / android.os.Debug / MediaScannerConnection


C++ API
=======

The functions below do not require to config gradle before using.

 1. Environment::getExternalStoragePublicDirectory
 1. MediaScannerConnection::scanFile
 1. Debug::getNativeHeapSize()
 1. Debug::getNativeHeapAllocatedSize()

QML Components
==============

```
    import AndroidNative 1.0
```

 1. ImagePicker
 1. Toast


SystemDispatcher
================

Automatic type convertion

| Qt           | Java    |
|--------------|---------|
| int          | int     |
| bool         | boolean |
| QString      | String  |
| QVariantList | List<T> |
| QVariantMap  | Map<T>  |


Installation Instruction
========================

[androidnative.pri/installation.md at master · benlau/androidnative.pri](https://github.com/benlau/androidnative.pri/blob/master/docs/installation.md)

Example
=======

An example program is available at: [androidnative.pri/examples/androidnativeexample at master · benlau/androidnative.pri](https://github.com/benlau/androidnative.pri/tree/master/examples/androidnativeexample)

![example1.png (533×472)](https://raw.githubusercontent.com/benlau/androidnative.pri/master/docs/screenshots/example1.png)
