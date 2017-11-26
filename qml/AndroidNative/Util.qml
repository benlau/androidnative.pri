import QtQuick 2.0
import AndroidNative 1.0

Item {

    /// A list of SMS messages
    property var smsMessages: []
    property int smsMessagesCount: 0

    signal smsFetched();
    signal callFinished();

    property string m_GETSMS_MESSAGE: "androidnative.Util.getSMSMessages";
    property string m_GOTSMS_MESSAGE: "androidnative.Util.gotSMSMessages";

    property string m_MAKE_CALL_MESSAGE: "androidnative.Util.makeCall";

    property bool callInitiated  : false;
    property bool callInProgress : false;


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

    function makeCall(params) {
        var args = {};
        if (params === undefined ) {
            params={};
        }
        if (params.number) {
            args.number = params.number;
        }

        // intent can be 'call' or 'dial' , default: dial
        args.intent = params.intent ? params.intent  : 'dial';


        callInitiated = true;
        SystemDispatcher.dispatch( m_MAKE_CALL_MESSAGE , args );
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

    Connections {
        target: Qt.application

        onStateChanged: {

            // When the new activity of calling is launched the Qt application eventually
            // gets suspended , just before suspension of the app the state changes to
            // Qt.ApplicationSuspended, this is our chance to register that callisin progress
            // note progress does not means call has really connected, as a matter of
            // fact for security reasons android does not returns the result of calling
            // intents.

            if ( callInitiated  &&  Qt.application.state ==Qt.ApplicationSuspended  ) {
                callInProgress = true;
            }

            // When the Qt applications regains active status , ie comes in foreground
            // then we check if call was in progress , in such a case emit the signal
            if (callInProgress  && Qt.application.state == Qt.ApplicationActive ) {
                // flip back the bools.
                 callInProgress = false;
                 callInitiated  = false;
                // emit signal.
                 callFinished();
            }
        }
    }


    Component.onCompleted: {
        // load the Java class Util in package androidnative
        SystemDispatcher.loadClass("androidnative.Util");
    }
}
