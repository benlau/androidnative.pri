#include "mediascannerconnection.h"
#include <AndroidNative/systemdispatcher.h>

static bool loadedClass = false;

void AndroidNative::MediaScannerConnection::scanFile(const QString &path)
{
    if (!loadedClass) {
        SystemDispatcher::instance()->loadClass("androidnative.MediaScanner");
        loadedClass = true;
    }

    QVariantMap map;
    map["path"] = path;
    SystemDispatcher::instance()->dispatch("androidnative.MediaScanner.scanFile", map);
}
