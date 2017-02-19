#ifndef ENVIRONMENT_H
#define ENVIRONMENT_H

#include <QString>

namespace AndroidNative {

    namespace Environment
    {
        extern QString DIRECTORY_DCIM;

        QString getExternalStoragePublicDirectory(const QString& path);
    };

}

#endif // ENVIRONMENT_H
