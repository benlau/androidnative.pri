import QtQuick 2.0
import AndroidNative 1.0

Item {

    /// A list of SMS messages
    property var smsMessages: []
    property int smsMessagesCount: 0

    signal smsFetched();

    property string m_GETSMS_MESSAGE: "androidnative.Util.getSMSMessages";
    property string m_GOTSMS_MESSAGE: "androidnative.Util.gotSMSMessages";

    function getSMSMessages(params) {

        var args = {};
        if (params === undefined ) {
            params={};
        }
        if (params.age)     args.age = params.age ;     // read sms whose age is < age seconds
        if (params.after)   args.after = params.after ; // read sms which are created after 'after' millisecs in epoch
                                                        // pass after as string.

        if (params.afterId) args.afterId = params.afterId ; // read sms whose _id > afterId
        if (params.read)    args.read = params.read ;       // read: 0 means unread SMS , read:1 means read SMS
        if (params.count)   args.count = params.count ;     // return upto count SMS
        if (params.match)   args.match = params.match ;     // return only those SMS whose body matches match
                                                            // SQL LIKE pattern expected. i.e body like '%match%'

        SystemDispatcher.dispatch( m_GETSMS_MESSAGE , args );
    }

    Connections {
        target: SystemDispatcher
        onDispatched: {
            if (type === m_GOTSMS_MESSAGE ) {
                smsMessages = message.messages ;
                smsMessagesCount = message.messagesCount;
                smsFetched();
            }
        }
    }

    Component.onCompleted: {
        // load the Java class Util in package androidnative
        SystemDispatcher.loadClass("androidnative.Util");
    }
}
