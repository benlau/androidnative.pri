package androidnative;

import android.app.Activity;
import org.qtproject.qt5.android.QtNative;
import java.util.Map;
import android.media.MediaScannerConnection;

/**
 * Created by benlau on 19/2/2017.
 */

public class MediaScanner {


    public static final String SCAN_FILE = "androidnative.MediaScanner.scanFile";

    static {
        SystemDispatcher.addListener(new SystemDispatcher.Listener() {
            public void onDispatched(String type, Map message) {
                if (type.equals(SCAN_FILE)) {
                    scanFile(message);
                }
            }
        });
    }

    static void scanFile(Map message) {
        Activity activity = org.qtproject.qt5.android.QtNative.activity();

        String path = (String) message.get("path");

        MediaScannerConnection.scanFile(activity, new String[] {path}, null, null);
    }

}