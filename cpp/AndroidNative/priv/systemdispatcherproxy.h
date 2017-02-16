#pragma once

#include <QObject>
#include <QVariantMap>

namespace AndroidNative {

    class SystemDispatcherProxy : public QObject
    {
        Q_OBJECT
    public:
        explicit SystemDispatcherProxy(QObject *parent = 0);
        ~SystemDispatcherProxy();

        Q_INVOKABLE void dispatch(QString type, QVariantMap message = QVariantMap());

        Q_INVOKABLE void loadClass(QString className);

    signals:
        void dispatched(QString type , QVariantMap message);

    };
}
