#include <QtCore>
#include "debug.h"
#ifdef Q_OS_ANDROID
#include <QAndroidJniEnvironment>
#include <QAndroidJniObject>
#endif

long AndroidNative::Debug::getNativeHeapSize()
{
#ifdef Q_OS_ANDROID
    QAndroidJniEnvironment env;

    jclass clazz = env->FindClass("android/os/Debug");
    if (clazz)
    {
        jmethodID mid = env->GetStaticMethodID(clazz, "getNativeHeapSize", "()J");
        if (mid)
        {
            return env->CallStaticLongMethod(clazz, mid);
        }
    }
    return -1L;
#else
    return -1L;
#endif
}


long AndroidNative::Debug::getNativeHeapAllocatedSize()
{
#ifdef Q_OS_ANDROID
    QAndroidJniEnvironment env;

    jclass clazz = env->FindClass("android/os/Debug");
    if (clazz)
    {
        jmethodID mid = env->GetStaticMethodID(clazz, "getNativeHeapAllocatedSize", "()J");
        if (mid)
        {
            return env->CallStaticLongMethod(clazz, mid);
        }
    }
    return -1L;
#else
    return -1L;
#endif
}
