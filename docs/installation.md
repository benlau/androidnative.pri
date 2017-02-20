AndroidNative Installation Guide
================================

Step 1 - Download source package
-----------------------------

It is recommended to install via qpm

    qpm install android.native.pri

Step 2 - Setup import path
---------------------------

Step 3 - JNI_OnLoad
-------------------

In you main.cpp, add following lines:

```
#ifdef Q_OS_ANDROID
#include <AndroidNative/systemdispatcher.h>
#include <QtAndroidExtras/QAndroidJniObject>
#include <QtAndroidExtras/QAndroidJniEnvironment>

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    Q_UNUSED(vm);
    qDebug("NativeInterface::JNI_OnLoad()");

    // It must call this function within JNI_OnLoad to enable System Dispatcher
    AndroidNative::SystemDispatcher::registerNatives();

    return JNI_VERSION_1_6;
}
#endif
```



Example: [TODO]

Step 4 - Modify build.gradle
----------------------------

AndroidNative only supports gradle build system, and you must have AndroidManifest.xml and build.gradle file is generated. If you don't know how to do, please check the document below about "Create templates"

[Deploying Applications to Android Devices | Qt Creator Manual](http://doc.qt.io/qtcreator/creator-deploying-android.html)


1) Copy androidnative.gradle from [TODO] to ANDROID_PACKAGE_SOURCE_DIR

2) Add the following line **at the end** of your build.gradle

```
apply from: "androidnative.gradle"
```

[Optional]

In case, AndroidNative is not installed via qpm, and ANDROID_PACKAGE_SOURCE_DIR is not set to the default path. You would need to modify  `androidnative.gradle` to get it works.

Open `androidnative.gradle` and find this line:


    appendAndroidNativePath("/../vendor/android/native/pri/java")

The input is the relative path from ANDROID_PACKAGE_SOURCE_DIR to the installation path of AndroidNative (plus java/ subfolder). Change it to fit your actual setup.

Step 5 - Modify your Android activity
-------------------------------------

If you don't have your own custom Android activity, modify AndroidManifest.xml, change activity.name to "androidnative.AndroidnativeActivity"

Example: TODO

If you have your own custom Android activity, add following lines to your activity class:

```
import quickandroid.SystemDispatcher;
```

```
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemDispatcher.onActivityResult(requestCode,resultCode,data);
    }
```

Example: TODO


