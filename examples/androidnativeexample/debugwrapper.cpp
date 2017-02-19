#include "debugwrapper.h"
#include "AndroidNative/debug.h"

DebugWrapper::DebugWrapper(QObject *parent) : QObject(parent)
{

}

long DebugWrapper::getNativeHeapSize() const
{
    return AndroidNative::Debug::getNativeHeapSize();
}

long DebugWrapper::getNativeHeapAllocatedSize() const
{
    return AndroidNative::Debug::getNativeHeapAllocatedSize();
}
