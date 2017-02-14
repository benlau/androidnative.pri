import QtQuick 2.2
import QtQuick.Window 2.1
import QuickAndroid 0.1
import QuickAndroid.Styles 0.1
import "./theme"

Page {
    objectName: "ComponentPage";

    property var pages: [

        {
            name : "Dialog",
            demo: "dialog/DialogDemo.qml",
            description: "Dialog Component"
        },
        {
            name: "Image Picker",
            demo: "imagePicker/ImagePickerDemo.qml",
            description: "Pick interface via Java language binding"
        },{
            name: "Notification",
            demo: "notification/NotificationDemo.qml",
            description: "Notification"
        }

    ];

    actionBar: ActionBar {
        id : actionBar
        iconSource: A.drawable("ic_menu",Constants.black87)
        title: "Component List"
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
