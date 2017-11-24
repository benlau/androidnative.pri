Calling Android functions from Qt without using JNI 
===========================

It is a fork of the [Quick Android](https://github.com/benlau/quickandroid) project that aims to provide a library to access Android functions from Qt/QML without using JNI.

Remarks: This project only support gradle build system.

Features
========

 1. SystemDispatcher - A message queue for C++/Qt and Java/Android
  1. Send message in C++, receive in Java and vice versa.
  1. Data type will be converted automatically (e.g QMap <-> java.util.Map). No need to write in JNI.
  1. It could be used to write your own Java code
 1. Bundled Components
  1. Image Picker
  1. Toast
  1. Video Picker
  1. SMS Inbox reader (via Util component)
  1. make a phone call, makeCall (via Util component)
 1. Wrapper of 	android.os.Environment / android.os.Debug / MediaScannerConnection

C++ API
=======

 1. Environment::getExternalStoragePublicDirectory()
 1. MediaScannerConnection::scanFile()
 1. Debug::getNativeHeapSize()
 1. Debug::getNativeHeapAllocatedSize()

The functions above do not require to config gradle before using.

QML Components
==============

```
    import AndroidNative 1.0
```

 1. ImagePicker
 1. VideoPicker
 1. Toast
 1. Util


SystemDispatcher
================

SystemDispatcher is a message queue component for delivering action message between C++/QML and Java. Data type in message is converted to the target platform automatically (e.g QMap <-> java.util.Map) . So that user doesn't need to write JNI to access their Java functions.

Moreover, SystemDispatcher is compilable on a non-Android platform. It doesn't cause any trouble to run on your development host.

```
// C++ Example

#include <AndroidNative>

// Load a Java class. It will trigger the code within `static` block
SystemDispatcher::instance()->loadClass("androidnative.example.ExampleService");

QVariantMap message;
message["value1"] = 1;
message["value2"] = 2.0;
message["value3"] = "3";

// Dispatch a message
SystemDispatcher::instance()->dispatch("androidnative.example.dummyAction", message);

```

```
// QML Example

import AndroidNative 1.0

Item {

  Component.onCompleted: {
    var message = {
      value1: 1,
      value2: 2.0,
      value3: "3"
    }
    SystemDispatcher.dispatch("androidnative.example.dummyAction", message);
  }
}
```

```
// Java Receiver

public class ExampleService {

    static {

        SystemDispatcher.addListener(new SystemDispatcher.Listener() {

            public void onDispatched(String type , Map message) {

                if (type.equals("androidnative.example.dummyAction")) {
                    // Process then dispatch a response back to C++/QML                    
                    SystemDispatcher.dispatch(("androidnative.example.response");
                
                    return;
                }
                return;
            }        
        });        
     }
}

```

```
// Listen from QML

Connections {
  target: SystemDispatcher
  onDispatched: {
    // process the message
  }
}
```


Supported data type:

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
