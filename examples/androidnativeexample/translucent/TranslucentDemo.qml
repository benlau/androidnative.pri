import QtQuick 2.2
import QtQuick.Window 2.1
import QtQuick.Controls 2.1 as QQC2
import QuickAndroid 0.1
import QuickAndroid.Styles 0.1
import AndroidNative 1.0 as AN
import "../theme"

Page {

    actionBar: ActionBar {
        id : actionBar
        title: "Toast"
        z: 10
        upEnabled: true
        onActionButtonClicked: back();
    }

    AN.Toast {
        id: toast
        text: "Toast"
        longDuration: true
    }

    QQC2.Switch {
        text: "Translucent Status Bar"
        checked: false
        anchors.centerIn: parent

        onCheckedChanged: {
            AN.SystemDispatcher.dispatch("androidnative.Util.setTranslucentStatusBar", {value: checked});
        }
    }

    Component.onCompleted: {
        AN.SystemDispatcher.loadClass("androidnative.Util");
    }


}
