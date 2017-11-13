// Author:  Ben Lau (https://github.com/benlau)
#include <QCoreApplication>
#include <QPointer>
#include <QtCore>
#include <QPair>
#include <QQueue>
#include "systemdispatcher.h"

using namespace AndroidNative;

static QPointer<SystemDispatcher> m_instance;

QString SystemDispatcher::ACTIVITY_RESUME_MESSAGE = "Activity.onResume";
QString SystemDispatcher::ACTIVITY_RESULT_MESSAGE = "Activity.onActivityResult";
static bool registerNativesCalled = false;

#ifdef Q_OS_ANDROID
#include <QAndroidJniObject>
#include <QAndroidJniEnvironment>

#define JCLASS_Name "androidnative/SystemDispatcher"
#define DISPATCH_SIGNATURE "(Ljava/lang/String;Ljava/util/Map;)V"
#define EMIT_SIGNATURE "(Ljava/lang/String;Ljava/util/Map;)V"

static QVariantMap createVariantMap(jobject data);
static jobject createHashMap(const QVariantMap &data);

static QVariant convertToQVariant(QAndroidJniObject value) {
    QVariant v;
    if (!value.isValid()) {
        return v;
    }

    QAndroidJniEnvironment env;

    jclass jclass_of_string = env->FindClass("java/lang/String");
    jclass jclass_of_integer = env->FindClass("java/lang/Integer");
    jclass jclass_of_long = env->FindClass("java/lang/Long");
    jclass jclass_of_float = env->FindClass("java/lang/Float");
    jclass jclass_of_double = env->FindClass("java/lang/Double");
    jclass jclass_of_boolean = env->FindClass("java/lang/Boolean");
    jclass jclass_of_list = env->FindClass("java/util/List");
    jclass jclass_of_map = env->FindClass("java/util/Map");

    if (env->IsInstanceOf(value.object<jobject>(),jclass_of_boolean)) {
        v = QVariant::fromValue<bool>(value.callMethod<jboolean>("booleanValue","()Z"));
    } else if (env->IsInstanceOf(value.object<jobject>(),jclass_of_integer)) {
        v = value.callMethod<jint>("intValue","()I");
    } else if (env->IsInstanceOf(value.object<jobject>(),jclass_of_string)) {
        v = value.toString();
    } else if (env->IsInstanceOf(value.object<jobject>(),jclass_of_long)) {
        v = value.callMethod<jlong>("longValue","()J");
    } else if (env->IsInstanceOf(value.object<jobject>(),jclass_of_float)) {
        v = value.callMethod<jfloat>("floatValue","()F");
    } else if (env->IsInstanceOf(value.object<jobject>(),jclass_of_double)) {
        v = value.callMethod<jdouble>("doubleValue","()D");
    } else if (env->IsInstanceOf(value.object<jobject>(), jclass_of_map)) {
        v = createVariantMap(value.object<jobject>());
    } else if (env->IsInstanceOf(value.object<jobject>(),jclass_of_list)) {
        QVariantList list;
        int count = value.callMethod<jint>("size","()I");
        for (int i = 0 ; i < count ; i++) {
            QAndroidJniObject item = value.callObjectMethod("get","(I)Ljava/lang/Object;",i);
            list.append(convertToQVariant(item));
        }
        v = list;
    } else {
         qWarning() << "value is not an instance of any of the handled jclass types\n";
    }

    env->DeleteLocalRef(jclass_of_string);
    env->DeleteLocalRef(jclass_of_integer);
    env->DeleteLocalRef(jclass_of_long);
    env->DeleteLocalRef(jclass_of_float);
    env->DeleteLocalRef(jclass_of_double);
    env->DeleteLocalRef(jclass_of_boolean);
    env->DeleteLocalRef(jclass_of_list);
    env->DeleteLocalRef(jclass_of_map);

    return v;
}

static QVariantMap createVariantMap(jobject data) {
    QVariantMap res;

    QAndroidJniEnvironment env;
    /* Reference : https://community.oracle.com/thread/1549999 */

    // Get the HashMap Class
    jclass jclass_of_hashmap = (env)->GetObjectClass(data);

    // Get link to Method "entrySet"
    jmethodID entrySetMethod = (env)->GetMethodID(jclass_of_hashmap, "entrySet", "()Ljava/util/Set;");

    // Invoke the "entrySet" method on the HashMap object
    jobject jobject_of_entryset = env->CallObjectMethod(data, entrySetMethod);

    // Get the Set Class
    jclass jclass_of_set = (env)->FindClass("java/util/Set"); // Problem during compilation !!!!!

    if (jclass_of_set == 0) {
         qWarning() << "java/util/Set lookup failed\n";
         return res;
    }

    // Get link to Method "iterator"
    jmethodID iteratorMethod = env->GetMethodID(jclass_of_set, "iterator", "()Ljava/util/Iterator;");

    // Invoke the "iterator" method on the jobject_of_entryset variable of type Set
    jobject jobject_of_iterator = env->CallObjectMethod(jobject_of_entryset, iteratorMethod);

    // Get the "Iterator" class
    jclass jclass_of_iterator = (env)->FindClass("java/util/Iterator");

    // Get link to Method "hasNext"
    jmethodID hasNextMethod = env->GetMethodID(jclass_of_iterator, "hasNext", "()Z");

    jmethodID nextMethod = env->GetMethodID(jclass_of_iterator, "next", "()Ljava/lang/Object;");

    while (env->CallBooleanMethod(jobject_of_iterator, hasNextMethod) ) {
        jobject jEntry = env->CallObjectMethod(jobject_of_iterator,nextMethod);
        QAndroidJniObject entry = QAndroidJniObject(jEntry);
        QAndroidJniObject key = entry.callObjectMethod("getKey","()Ljava/lang/Object;");
        QAndroidJniObject value = entry.callObjectMethod("getValue","()Ljava/lang/Object;");
        QString k = key.toString();

        QVariant v = convertToQVariant(value);

        env->DeleteLocalRef(jEntry);

        if (v.isNull()) {
            continue;
        }

        res[k] = v;
    }

    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    env->DeleteLocalRef(jclass_of_hashmap);
    env->DeleteLocalRef(jobject_of_entryset);
    env->DeleteLocalRef(jclass_of_set);
    env->DeleteLocalRef(jobject_of_iterator);
    env->DeleteLocalRef(jclass_of_iterator);

    return res;
}

static jobject convertToJObject(QVariant v) {
    jobject res = 0;
    QAndroidJniEnvironment env;

    if (v.type() == QVariant::String) {
        QString str = v.toString();
        res = env->NewStringUTF(str.toLocal8Bit().data());
    } else if (v.type() == QVariant::Int) {
        jclass integerClass = env->FindClass("java/lang/Integer");
        jmethodID integerConstructor = env->GetMethodID(integerClass, "<init>", "(I)V");

        res = env->NewObject(integerClass,integerConstructor,v.toInt());

        env->DeleteLocalRef(integerClass);
    } else if (v.type() == QVariant::LongLong) {
        jclass longClass = env->FindClass("java/lang/Long");
        jmethodID longConstructor = env->GetMethodID( longClass, "<init>", "(J)V");

        res = env->NewObject(longClass,longConstructor, v.toLongLong()  );

        env->DeleteLocalRef(longClass);

    } else if (v.type() == QVariant::Double) {
        jclass doubleClass = env->FindClass("java/lang/Double");
        jmethodID doubleConstructor = env->GetMethodID( doubleClass, "<init>", "(D)V");

        res = env->NewObject(doubleClass,doubleConstructor,v.toDouble());

        env->DeleteLocalRef(doubleClass);

    } else if (v.canConvert<float>()) {
        jclass floatClass = env->FindClass("java/lang/Float");
        jmethodID floatConstructor = env->GetMethodID( floatClass, "<init>", "(F)V");

        res = env->NewObject(floatClass,floatConstructor,v.toFloat());

        env->DeleteLocalRef(floatClass);

    } else if (v.type() == QVariant::Bool) {
        jclass booleanClass = env->FindClass("java/lang/Boolean");
        jmethodID booleanConstructor = env->GetMethodID(booleanClass,"<init>","(Z)V");

        res = env->NewObject(booleanClass,booleanConstructor,v.toBool());

        env->DeleteLocalRef(booleanClass);

    } else if (v.type() == QVariant::Map) {
        res = createHashMap(v.toMap());
    } else  if (v.type() == QVariant::List){
        QVariantList list = v.value<QVariantList>();
        jclass arrayListClass = env->FindClass("java/util/ArrayList");
        jmethodID init = env->GetMethodID(arrayListClass, "<init>", "(I)V");
        res = env->NewObject( arrayListClass, init, list.size());

        jmethodID add = env->GetMethodID( arrayListClass, "add",
                    "(Ljava/lang/Object;)Z");

        for (int i = 0 ; i < list.size() ; i++) {
            jobject item = convertToJObject(list.at(i));
            env->CallBooleanMethod(res,add, item);
            env->DeleteLocalRef(item);
        }

        env->DeleteLocalRef(arrayListClass);
    } else {
        qWarning() << "ANSystemDispatcher: Non-supported data type - " <<  v.type();
    }
    return res;
}

static jobject createHashMap(const QVariantMap &data) {
    QAndroidJniEnvironment env;

    jclass mapClass = env->FindClass("java/util/HashMap");

    if (mapClass == NULL)  {
        qWarning() << "Failed to find class" << "java/util/HashMap";
        return NULL;
    }

    jsize map_len = data.size();

    jmethodID init = env->GetMethodID(mapClass, "<init>", "(I)V");
    jobject hashMap = env->NewObject( mapClass, init, map_len);

    jmethodID put = env->GetMethodID( mapClass, "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    QMapIterator<QString, QVariant> iter(data);
    while (iter.hasNext()) {
        iter.next();

        QString key = iter.key();
        jstring jkey = env->NewStringUTF(key.toLocal8Bit().data());
        QVariant v = iter.value();
        jobject item = convertToJObject(v);

        if (item == 0) {
            continue;
        }

        env->CallObjectMethod(hashMap,put,jkey,item);
        env->DeleteLocalRef(item);
        env->DeleteLocalRef(jkey);
     }

    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    env->DeleteLocalRef(mapClass);

    return hashMap;
}

static void jniEmit(JNIEnv* env,jobject object,jstring name,jobject data) {
    Q_UNUSED(object);
    Q_UNUSED(env);

    QAndroidJniObject tmp(name);
    QString str = tmp.toString();

    QVariantMap map;

    if (data != 0) {
        map = createVariantMap(data);
    }

    if (m_instance.isNull()) {
        return;
    }

    QMetaObject::invokeMethod(m_instance.data(),"dispatched",Qt::AutoConnection,
                              Q_ARG(QString, str),
                              Q_ARG(QVariantMap,map));
}

static void printInstruction() {
    static bool printOnce = false;

    if (printOnce) {
        return;
    }
    printOnce = true;

    // BuildMyString.com generated code. Please enjoy your string responsibly.

    QString msg =  "Using SystemDispatcher in Android but SystemDispatcher::registerNatives() is never called. Instruction: \r\n"
           "https://github.com/benlau/androidnative.pri/blob/master/docs/installation.md\r\n";

   qWarning() << msg;
}
#endif

AndroidNative::SystemDispatcher::SystemDispatcher(QObject* parent) : QObject(parent)
{
}

AndroidNative::SystemDispatcher::~SystemDispatcher()
{
}

AndroidNative::SystemDispatcher *SystemDispatcher::instance()
{
    if (!m_instance) {
        QCoreApplication* app = QCoreApplication::instance();
        m_instance = new SystemDispatcher(app);
    }
    return m_instance;
}

void AndroidNative::SystemDispatcher::dispatch(QString type, QVariantMap message)
{
    Q_UNUSED(type);
    Q_UNUSED(message);

#ifdef Q_OS_ANDROID
    if (!registerNativesCalled) {
        printInstruction();
    }

    QAndroidJniEnvironment env;

    jstring jType = env->NewStringUTF(type.toLocal8Bit().data());
    jobject jData = createHashMap(message);
    QAndroidJniObject::callStaticMethod<void>(JCLASS_Name, "dispatch",
                                              DISPATCH_SIGNATURE,
                                              jType,jData);
    env->DeleteLocalRef(jType);
    env->DeleteLocalRef(jData);

#else
    static bool dispatching = false;
    static QQueue<QPair<QString,QVariantMap> > queue;


    if (dispatching) {
        queue.enqueue(QPair<QString,QVariantMap> (type,message) );
        return;
    }

    dispatching = true;
    emit dispatched(type,message);

    while (queue.size() > 0) {
        QPair<QString,QVariantMap> pair = queue.dequeue();
        emit dispatched(pair.first,pair.second);
    }
    dispatching = false;
#endif
}

void AndroidNative::SystemDispatcher::loadClass(QString javaClassName)
{
    QVariantMap message;
    message["className"] = javaClassName;

    dispatch("androidnative.SystemDispatcher.loadClass",message);
}

void AndroidNative::SystemDispatcher::registerNatives()
{
    Q_UNUSED(registerNativesCalled);

#ifdef Q_OS_ANDROID
    registerNativesCalled = true;

    QAndroidJniEnvironment env;
    jclass clazz = env->FindClass(JCLASS_Name);
    if (!clazz)
    {
        qCritical() <<  "androidNative.SystemDispatcher Java class is not found. Please configure your build.gradle according to this instruction: \r\n"
                        "https://github.com/benlau/androidnative.pri/blob/master/docs/installation.md\r\n";
        if (env->ExceptionOccurred()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        return;
    }

    JNINativeMethod methods[] =
    {
        {"jniEmit", EMIT_SIGNATURE, (void *)&jniEmit},
    };

    int numMethods = sizeof(methods) / sizeof(methods[0]);
    if (env->RegisterNatives(clazz, methods, numMethods) < 0) {
        if (env->ExceptionOccurred()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
            qCritical() << "Exception occurred!!!";
            return;
        }
    }

    QAndroidJniObject::callStaticMethod<void>(JCLASS_Name, "init",
                                              "()V");
#endif
}
