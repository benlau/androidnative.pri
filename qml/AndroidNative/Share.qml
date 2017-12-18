import QtQuick 2.0
import AndroidNative 1.0

Item {

    property string m_SHARE_CONTENT: "androidnative.Util.shareContent";

    function shareContent(params) {
        var args = {};
        if (params === undefined ) {
            params={};
        }
        var arglist = new Array (
                    'mime_type'
                    , 'text'  , 'uri' , 'url' , 'html_text'
                    , 'texts' , 'uris' , 'urls' , 'html_texts'
                    , 'email' , 'cc' , 'bcc' , 'subject'
                    , 'package'
        );
        arglist.map (
                    function (name) {
                        if (params[name] !== undefined )
                             args[name] = params[name];
                    }
        );

        args.mime_type = params.mime_type ? params.mime_type  : 'text/html';

        args["title"] = args["title"] || "Share Content";

        SystemDispatcher.dispatch( m_SHARE_CONTENT , args );
    }

    Component.onCompleted: {
        // load the Java class Util in package androidnative
        SystemDispatcher.loadClass("androidnative.Share");
    }
}
