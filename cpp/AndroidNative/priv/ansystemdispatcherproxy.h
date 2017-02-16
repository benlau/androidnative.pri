#ifndef QASYSTEMDISPATCHERPROXY_H
#define QASYSTEMDISPATCHERPROXY_H

#include <QObject>
#include <QVariantMap>

class ANSystemDispatcherProxy : public QObject
{
    Q_OBJECT
public:
    explicit ANSystemDispatcherProxy(QObject *parent = 0);
    ~ANSystemDispatcherProxy();

    Q_INVOKABLE void dispatch(QString type, QVariantMap message = QVariantMap());

    Q_INVOKABLE void loadClass(QString className);

signals:
    void dispatched(QString type , QVariantMap message);

};

#endif // QASYSTEMDISPATCHERPROXY_H
