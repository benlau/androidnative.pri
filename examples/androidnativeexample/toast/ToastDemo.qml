import QtQuick 2.2
import QtQuick.Window 2.1
import QuickAndroid 0.1
import QuickAndroid.Styles 0.1
import AndroidNative 1.0 as AN
import "../theme"

Page {

    actionBar: ActionBar {
        id : actionBar
        title: "Notification"
        z: 10
        upEnabled: true
        onActionButtonClicked: back();
    }

    AN.Toast {
        id: toast
        text: "Toast"
        longDuration: true
    }

    Button {
        id: label
        text : "Press to show toast"
        anchors.centerIn: parent
        onClicked: {
            toast.show();
        }
    }

}
