
QML_IMPORT_PATH += $$PWD

INCLUDEPATH += $$PWD/cpp
RESOURCES += \
    $$PWD/androidnative.qrc

HEADERS += \
    $$PWD/cpp/AndroidNative/priv/systemdispatcherproxy.h \
    $$PWD/cpp/AndroidNative/systemdispatcher.h

SOURCES += \
    $$PWD/cpp/AndroidNative/priv/systemdispatcherproxy.cpp \
    $$PWD/cpp/AndroidNative/systemdispatcher.cpp

