import QtQuick 2.0
import QuickAndroid 0.1
import QuickAndroid.Styles 0.1
import "../theme"

Page {
    id: page
    actionBar: ActionBar {
        id: actionBar
        upEnabled: true
        title: qsTr("Information")
        showTitle: true

        onActionButtonClicked: back();
        z: 10
    }

    VisualItemModel {
        id: itemModel

        ListItem {
            title: "Environment.DIRECTORY_DCIM"
            subtitle: Environment.DIRECTORY_DCIM
            interactive: false
            width: page.width
        }
    }


    ListView {
        anchors.fill: parent
        model: itemModel
    }
}
