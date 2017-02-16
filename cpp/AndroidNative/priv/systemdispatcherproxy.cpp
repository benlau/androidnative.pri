#include <QtQml>
#include "systemdispatcherproxy.h"
#include "AndroidNative/systemdispatcher.h"

AndroidNative::SystemDispatcherProxy::SystemDispatcherProxy(QObject *parent) : QObject(parent)
{
    connect(SystemDispatcher::instance(),SIGNAL(dispatched(QString,QVariantMap)),
            this,SIGNAL(dispatched(QString,QVariantMap)));

}

AndroidNative::SystemDispatcherProxy::~SystemDispatcherProxy()
{

}

void AndroidNative::SystemDispatcherProxy::dispatch(QString type, QVariantMap message)
{
    SystemDispatcher::instance()->dispatch(type,message);
}

void AndroidNative::SystemDispatcherProxy::loadClass(QString className)
{
    SystemDispatcher::instance()->loadClass(className);
}
