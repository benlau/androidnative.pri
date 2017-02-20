#include <QtQml>
#include <AndroidNative/systemdispatcher.h>
#include <AndroidNative/priv/systemdispatcherproxy.h>

using namespace AndroidNative;

static QObject *systemDispatcherProvider(QQmlEngine *engine, QJSEngine *scriptEngine) {
    Q_UNUSED(engine);
    Q_UNUSED(scriptEngine);

    SystemDispatcherProxy* object = new SystemDispatcherProxy();

    return object;
}



static void regiserQmlTypes() {
    // QADevice is a exception. Won't register by QAQmlTypes.

    qmlRegisterSingletonType<SystemDispatcherProxy>("AndroidNative", 1, 0,
                                                    "SystemDispatcher",
                                                     systemDispatcherProvider);
}

Q_COREAPP_STARTUP_FUNCTION(regiserQmlTypes)
