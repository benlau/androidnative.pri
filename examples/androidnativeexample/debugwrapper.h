#ifndef DEBUGWRAPPER_H
#define DEBUGWRAPPER_H

#include <QObject>

class DebugWrapper : public QObject
{
    Q_OBJECT
public:
    explicit DebugWrapper(QObject *parent = 0);

signals:

public slots:
    long getNativeHeapSize() const;

    long getNativeHeapAllocatedSize() const;

};

#endif // DEBUGWRAPPER_H
