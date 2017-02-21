#include <QtCore>
#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQuickView>
#include <QQmlContext>
#include "qadrawableprovider.h"
#include "AndroidNative/systemdispatcher.h"
#include "AndroidNative/environment.h"
#include "AndroidNative/debug.h"
#include "AndroidNative/mediascannerconnection.h"
#include "debugwrapper.h"

using namespace AndroidNative;

#ifdef Q_OS_ANDROID
#include <QtAndroidExtras/QAndroidJniObject>
#include <QtAndroidExtras/QAndroidJniEnvironment>

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    Q_UNUSED(vm);
    qDebug("NativeInterface::JNI_OnLoad()");

    // It must call this function within JNI_OnLoad to enable System Dispatcher
    SystemDispatcher::registerNatives();
    return JNI_VERSION_1_6;
}
#endif

int main(int argc, char *argv[])
{
#if (QT_VERSION >= QT_VERSION_CHECK(5, 6, 0))
    QGuiApplication::setAttribute(Qt::AA_EnableHighDpiScaling);
#endif

    QGuiApplication app(argc, argv);

    QVariantMap env;
    env["DIRECTORY_DCIM"] = Environment::getExternalStoragePublicDirectory(Environment::DIRECTORY_DCIM);

    MediaScannerConnection::scanFile("");

    SystemDispatcher::instance()->loadClass("androidnative.example.ExampleService");

    QQmlApplicationEngine engine;

    /* QuickAndroid Initialization */
    engine.addImportPath("qrc:///"); // Add QuickAndroid into the import path
    engine.rootContext()->setContextProperty("Environment", env);
    engine.rootContext()->setContextProperty("Debug", new DebugWrapper(&engine));

    /* End of QuickAndroid Initialization */

    // Extra features:
    QADrawableProvider* provider = new QADrawableProvider();
    provider->setBasePath("qrc://res");
    engine.addImageProvider("drawable",provider);
    engine.load(QUrl(QStringLiteral("qrc:///main.qml")));

    return app.exec();
}
