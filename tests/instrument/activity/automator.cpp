#include <QtCore>
#include "AndroidNative/systemdispatcher.h"
#include "automator.h"

using namespace AndroidNative;

Automator::Automator(QObject *parent) : QObject(parent)
{

}

Automator::~Automator()
{

}

void Automator::start()
{
    connect(SystemDispatcher::instance(),SIGNAL(dispatched(QString,QVariantMap)),
            this,SLOT(onDispatched(QString,QVariantMap)));
}

void Automator::onDispatched(QString name, QVariantMap message)
{
    qDebug() << "Automator::receive" << name;
    if (name == "Automater::echo") {
        SystemDispatcher::instance()->dispatch("Automater::response",message);
    }
}

