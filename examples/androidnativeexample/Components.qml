import QtQuick 2.2
import QtQuick.Window 2.1
import QuickAndroid 0.1
import QuickAndroid.Styles 0.1
import "./theme"

Page {
    objectName: "ComponentPage";

    property var pages: [
        {
            name: "Image Picker",
            demo: "imagePicker/ImagePickerDemo.qml",
            description: "Pick photo via Java language binding"
        },{
            name: "Toast",
            demo: "toast/ToastDemo.qml",
            description: "Toast Demonstration"
        },{
            name: "Notification",
            demo: "notification/NotificationDemo.qml",
            description: "Demonstrate how to use SystemDispatcher to send notification"
        },{
            name: "Information",
            demo: "info/InfoDemo.qml",
            description: "Android System Information"
        },{
            name: "Translucent Status Bar",
            demo: "translucent/TranslucentDemo.qml",
            description: "Enable/disable Translucent Status Bar"
        }

    ];

    actionBar: ActionBar {
        id : actionBar
        iconSource: A.drawable("ic_menu",Constants.black87)
        title: "AndroidNative Component List"
        showIcon: false
        actionButtonEnabled: false
    }

    VisualDataModel {
        id: visualDataModel
        delegate: ListItem {
            title: modelData.name
            subtitle: modelData.description
            onClicked: {
                present(Qt.resolvedUrl(modelData.demo));
            }
        }

        model: pages;
    }

    ListView {
        anchors.fill: parent

        model : visualDataModel
    }

}
