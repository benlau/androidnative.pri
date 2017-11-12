package androidnative;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException ;

import org.qtproject.qt5.android.QtNative;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;




public class Util {

    private static final String TAG = "androidnative.Util";

    public static final String SET_TRANSLUCENT_STATUS_BAR = "androidnative.Util.setTranslucentStatusBar";
    public static final String SET_FULL_SCREEN = "androidnative.Util.setFullScreen";
    public static final String GET_SMS_MESSAGE = "androidnative.Util.getSMSMessages";
    public static final String GOT_SMS_MESSAGE = "androidnative.Util.gotSMSMessages";


    static {
        SystemDispatcher.addListener(new SystemDispatcher.Listener() {
            public void onDispatched(String type, Map message) {
                if (type.equals(SET_TRANSLUCENT_STATUS_BAR)) {
                    setTranslucentStatusBar(message);
                } else if (type.equals(SET_FULL_SCREEN)) {
                    setFullScreen(message);
                } else if (type.equals(GET_SMS_MESSAGE)) {
                    getSMSMessages(message);
                }
            }
        });
    }

    static void setTranslucentStatusBar(Map message) {
        if (Build.VERSION.SDK_INT < 21 ) {  //TODO: use Build.VERSION_CODES.LOLLIPOP  in place of 21
            return;
        }

        final Boolean value = (Boolean) message.get("value");
        final Activity activity = QtNative.activity();

        Runnable runnable = new Runnable () {
            public void run() {
                Window w = activity.getWindow(); // in Activity's onCreate() for instance

                if (value) {
//                    w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                } else {
//                    w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        };

        activity.runOnUiThread(runnable);

    }

    static void setFullScreen(Map message) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        final Boolean value = (Boolean) message.get("value");
        final Activity activity = QtNative.activity();

        Runnable runnable = new Runnable () {
            public void run() {
                Window w = activity.getWindow(); // in Activity's onCreate() for instance
                View decorView = w.getDecorView();

                int config = decorView.getSystemUiVisibility();

                if (value) {
                    config &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
                } else {
                    config |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                }
                decorView.setSystemUiVisibility(config);
            }
        };

        activity.runOnUiThread(runnable);

    }

    static void getSMSMessages(Map message) {
            Log.d(TAG, "Invoked JAVA getSMSMessages" );


            // params are age , read , match , count

            Activity activity = org.qtproject.qt5.android.QtNative.activity();

            Uri uriSms = Uri.parse("content://sms/inbox");
            String[] projection = new String[] { "_id", "address", "date", "body" };

            ArrayList<Map> smslist = new ArrayList();

            long cutOffTimeStamp = 0;
            try {
                String filter = " 1=1 " ;

                if ( message.containsKey("age") ) {
                    int   age = (int) message.get("age");   // age in seconds
                    cutOffTimeStamp = System.currentTimeMillis() - age * 1000 ;
                    filter = filter + " and date >= " + cutOffTimeStamp ;
                    }

                if ( message.containsKey("after") ) {
                    String after = (String) message.get("after") ;   // after is milliseconds since epoch
                    filter = filter + " and date > " + after  ;
                 }

                if ( message.containsKey("afterId") ) {
                    int   afterId = (int) message.get("afterId");
                    filter = filter + " and _id > " + afterId ;
                 }

                if ( message.containsKey("read") ) {
                    int read_status = (int) message.get("read");
                    filter = filter + " and read = " + read_status ;
                    }

                String sortOrder  = " date desc " ;

                if ( message.containsKey("count") ) {
                    int count = (int) message.get("count");
                    sortOrder = sortOrder + " limit " +  count ;
                    }

                String[] selectionArgs = {""};
                if ( message.containsKey("match") ) {
                    String match = (String) message.get("match");
                    filter = filter + " and body like ? "  ;
                    selectionArgs[0] = '%' + match + '%' ;
                 }

                Log.d(TAG,  "SMS Filter is : " + filter );

                Cursor cursor = activity.getContentResolver().query(uriSms, projection , filter, selectionArgs , sortOrder );


                int mesgCount = cursor.getCount();
                Log.d(TAG,  "MessagesCount = " + mesgCount );
                smslist.ensureCapacity(mesgCount);

                if (cursor != null)
                {
                    int index_id = cursor.getColumnIndex("_id");
                    int index_address = cursor.getColumnIndex("address");
                    int index_body = cursor.getColumnIndex("body");
                    int index_date = cursor.getColumnIndex("date");

                    while (cursor.moveToNext()) {
                        String sms_id = cursor.getString(index_id);
                        String address = cursor.getString(index_address);
                        String body = cursor.getString(index_body);
                        Long d = cursor.getLong(index_date);

                        Map sms = new HashMap();

                        sms.put("id", sms_id);
                        sms.put("address", address);
                        sms.put("body", body);
                        sms.put("date", d);
                        smslist.add( sms );
                      }

                    if (!cursor.isClosed()) {
                        cursor.close();
                        cursor = null;
                        }

                    } else {
                    Log.d(TAG, "cursor is not defined");
                    }


                } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
             }
            Map reply = new HashMap();
            reply.put("messages", smslist );
            reply.put("messagesCount", smslist.size() );
            SystemDispatcher.dispatch(GOT_SMS_MESSAGE,reply);
    }
}
