
QML_IMPORT_PATH += $$PWD

INCLUDEPATH += $$PWD/cpp
RESOURCES += \
    $$PWD/androidnative.qrc

HEADERS += \
    $$PWD/cpp/AndroidNative/priv/ansystemdispatcherproxy.h \
    $$PWD/cpp/AndroidNative/ansystemdispatcher.h

SOURCES += \
    $$PWD/cpp/AndroidNative/ansystemdispatcher.cpp \
    $$PWD/cpp/AndroidNative/priv/ansystemdispatcherproxy.cpp

