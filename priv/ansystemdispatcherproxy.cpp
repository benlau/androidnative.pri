#include <QtQml>
#include "ansystemdispatcherproxy.h"
#include "ansystemdispatcher.h"

ANSystemDispatcherProxy::ANSystemDispatcherProxy(QObject *parent) : QObject(parent)
{
    connect(ANSystemDispatcher::instance(),SIGNAL(dispatched(QString,QVariantMap)),
            this,SIGNAL(dispatched(QString,QVariantMap)));

}

ANSystemDispatcherProxy::~ANSystemDispatcherProxy()
{

}

void ANSystemDispatcherProxy::dispatch(QString type, QVariantMap message)
{
    ANSystemDispatcher::instance()->dispatch(type,message);
}

void ANSystemDispatcherProxy::loadClass(QString className)
{
    ANSystemDispatcher::instance()->loadClass(className);
}
