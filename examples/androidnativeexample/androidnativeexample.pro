TEMPLATE = app

QT += qml quick

SOURCES += main.cpp \
    debugwrapper.cpp

RESOURCES += qml.qrc

# Additional import path used to resolve QML modules in Qt Creator's code model
# QML_IMPORT_PATH += ../..

android {
    ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android-sources
}

include(vendor/vendor.pri)
# Default rules for deployment.
include(deployment.pri)
include(../../androidnative.pri)

DISTFILES += \
    android-sources/AndroidManifest.xml \
    android-sources/src/quickandroid/example/ExampleService.java \
    README.md \
    android-sources/gradle/wrapper/gradle-wrapper.jar \
    android-sources/gradlew \
    android-sources/res/values/libs.xml \
    android-sources/build.gradle \
    android-sources/gradle/wrapper/gradle-wrapper.properties \
    android-sources/gradlew.bat \
    android-sources/settings.gradle

HEADERS += \
    ../../README.md \
    debugwrapper.h
