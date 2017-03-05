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
        title: "Status Bar"
        z: 10
        upEnabled: true
        onActionButtonClicked: back();
    }

    Column {
        anchors.centerIn: parent

        QQC2.Switch {
            text: "Translucent Status Bar"
            checked: false

            onCheckedChanged: {
                AN.SystemDispatcher.dispatch("androidnative.Util.setTranslucentStatusBar", {value: checked});
            }
        }

        QQC2.Switch {
            text: "Status Bar Visible"
            checked: true

            onCheckedChanged: {
                AN.SystemDispatcher.dispatch("androidnative.Util.setFullScreen", {value: checked});
            }
        }

    }

    Component.onCompleted: {
        AN.SystemDispatcher.loadClass("androidnative.Util");
    }


}
