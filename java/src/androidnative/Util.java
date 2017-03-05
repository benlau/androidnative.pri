package androidnative;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.qtproject.qt5.android.QtNative;

import java.util.Map;

public class Util {

    private static final String TAG = "androidnative.Util";

    public static final String SET_TRANSLUCENT_STATUS_BAR = "androidnative.Util.setTranslucentStatusBar";
    public static final String SET_FULL_SCREEN = "androidnative.Util.setFullScreen";


    static {
        SystemDispatcher.addListener(new SystemDispatcher.Listener() {
            public void onDispatched(String type, Map message) {
                if (type.equals(SET_TRANSLUCENT_STATUS_BAR)) {
                    setTranslucentStatusBar(message);
                } else if (type.equals(SET_FULL_SCREEN)) {
                    setFullScreen(message);
                }
            }
        });
    }

    static void setTranslucentStatusBar(Map message) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
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
}
