#ifndef ENVIRONMENT_H
#define ENVIRONMENT_H

#include <QString>

namespace AndroidNative {

    class Environment
    {
    public:
        Environment();

        static QString DIRECTORY_DCIM;

        static QString getExternalStoragePublicDirectory(const QString& path);
    };

}

#endif // ENVIRONMENT_H
