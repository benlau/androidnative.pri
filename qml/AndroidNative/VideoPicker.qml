import QtQuick 2.0
import AndroidNative 1.0

Item {

    /// Set it to true if multiple videos should be picked.
    property bool multiple: false

    /// If it is true, it will broadcast the taked photo to other application (e.g Let it show in Google Photos)
    property bool broadcast: true

    /// The URL of the videos chosen. If multiple videos are picked, it will be equal to the first videos.
    property string videoUrl: ""

    /// The URL of the thumbnail of video chosen. If multiple videos are picked,
    // it will be equal to the thumbnail of first video.
    property string thumbUrl: ""

    /// A list of videos chosen
    property var videoUrls: []

    /// A list of thumbnails of videos chosen
    property var thumbUrls: []

    /// It is emitted whatever photo(s) are picked/taken.
    signal ready();

    function pickVideo() {
        SystemDispatcher.dispatch(m_PICK_VIDEO_MESSAGE,{ multiple: multiple });
    }

    function takeVideo() {
        SystemDispatcher.dispatch(m_TAKE_VIDEO_MESSAGE,{ broadcast: broadcast });
    }

    property string m_PICK_VIDEO_MESSAGE: "androidnative.VideoPicker.pickVideo";

    property string m_TAKE_VIDEO_MESSAGE: "androidnative.VideoPicker.takeVideo";

    property string m_CHOSEN_MESSAGE: "androidnative.VideoPicker.chosen";


    Connections {
        target: SystemDispatcher
        onDispatched: {
            if (type === m_CHOSEN_MESSAGE) {
                videoUrls = message.videoUrls;
                 videoUrl = videoUrls[0];

                thumbUrls = message.videoThumbUrls;
                thumbUrl  = thumbUrls[0];

                ready();
            }
        }
    }

    Component.onCompleted: {
        SystemDispatcher.loadClass("androidnative.VideoPicker");
    }
}

