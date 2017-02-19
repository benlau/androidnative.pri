#include <QStandardPaths>
#include <QDir>
#include "environment.h"
#ifdef Q_OS_ANDROID
#include <QAndroidJniEnvironment>
#include <QAndroidJniObject>
#endif

QString AndroidNative::Environment::DIRECTORY_DCIM = "DIRECTORY_DCIM";

QString AndroidNative::Environment::getExternalStoragePublicDirectory(const QString &type)
{
    QString res;

#ifdef Q_OS_ANDROID
    QAndroidJniObject jniType = QAndroidJniObject::getStaticObjectField<jstring>("android/os/Environment", type.toUtf8().constData());

    QAndroidJniObject file = QAndroidJniObject::callStaticObjectMethod("android/os/Environment",
                                                                       "getExternalStoragePublicDirectory",
                                                                       "(Ljava/lang/String;)Ljava/io/File;",
                                                                       jniType.object());
    QAndroidJniObject absolutePath = file.callObjectMethod("getAbsolutePath","()Ljava/lang/String;");
    res = absolutePath.toString();

#else
    if (type == DIRECTORY_DCIM) {
        res = QStandardPaths::standardLocations(QStandardPaths::PicturesLocation)[0];
    } else {
        res = QDir::currentPath();
    }
#endif
    return res;
}
