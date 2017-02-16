
QML_IMPORT_PATH += $$PWD

INCLUDEPATH += $$PWD
RESOURCES += \
    $$PWD/androidnative.qrc

HEADERS += \
    $$PWD/ansystemdispatcher.h \
    $$PWD/priv/ansystemdispatcherproxy.h

SOURCES += \
    $$PWD/ansystemdispatcher.cpp \
    $$PWD/priv/ansystemdispatcherproxy.cpp
