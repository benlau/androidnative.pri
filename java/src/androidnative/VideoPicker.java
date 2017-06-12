package androidnative;
import org.qtproject.qt5.android.QtNative;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.app.Activity;
import java.util.Map;
import android.net.Uri;
import java.util.HashMap;
import android.database.Cursor;
import android.content.ContentResolver ;
import android.content.ContentValues ;
import android.provider.MediaStore;
import android.media.ThumbnailUtils ;

import java.text.SimpleDateFormat;
import java.io.File;
import java.io.OutputStream;
import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.util.Date;
import android.os.Environment;
import android.content.ClipData;
import java.util.List;
import java.util.ArrayList;
import android.graphics.Bitmap ;





public class VideoPicker {

    // Random , generated using : $ openssl rand -hex 3
    public static final int PICK_VIDEO_ACTION = 0x7c43f0;
    public static final int TAKE_VIDEO_ACTION = 0x4df0bc;


    public static final String PICK_VIDEO_MESSAGE = "androidnative.VideoPicker.pickVideo";
    public static final String TAKE_VIDEO_MESSAGE = "androidnative.VideoPicker.takeVideo";
    public static final String CHOSEN_MESSAGE = "androidnative.VideoPicker.chosen";

    private static final String TAG = "androidnative.VideoPicker";

    private static Uri mVideoUri;
    private static Boolean broadcast = false;

    static {
        SystemDispatcher.addListener(new SystemDispatcher.Listener() {
            public void onDispatched(String type , Map message) {
            Log.d(TAG,"onDispatched called with type :" + type  );
            if (type.equals(PICK_VIDEO_MESSAGE)) {
                Log.d(TAG,"type is PICK_VIDEO_MESSAGE"  );
                pickVideo(message);
            } else if (type.equals(TAKE_VIDEO_MESSAGE)) {
                Log.d(TAG,"type is TAKE_VIDEO_MESSAGE"  );
                takeVideo(message);
            } else if (type.equals(SystemDispatcher.ACTIVITY_RESULT_MESSAGE)) {
                Log.d(TAG,"type is ACTIVITY_RESULT_MESSAGE"  );
                onActivityResult(message);
            }
         }
      });
    }

    static void pickVideo(Map message) {
        Boolean multiple = false;
        Activity activity = org.qtproject.qt5.android.QtNative.activity();

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");

        if (message.containsKey("multiple")) {
            multiple = (Boolean) message.get("multiple");
        }

        if (multiple) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        // >= API 18
        activity.startActivityForResult(intent, PICK_VIDEO_ACTION);
    }

    static void takeVideo(Map message) {
        if (message.containsKey("broadcast")) {
            broadcast = (Boolean) message.get("broadcast");
        }

       Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
       Activity activity = org.qtproject.qt5.android.QtNative.activity();
       if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
           activity.startActivityForResult(takeVideoIntent, TAKE_VIDEO_ACTION );
       }
    }

    static private void onActivityResult(Map message) {       

        int resultCode = (Integer) message.get("resultCode");
        if (resultCode != Activity.RESULT_OK)
            return;
        int requestCode = (Integer) message.get("requestCode");
        Intent data = (Intent) message.get("data");

        if (requestCode == PICK_VIDEO_ACTION) {
            importVideo(data);
        } else if (requestCode == TAKE_VIDEO_ACTION) {
            // Android 4.x. data will be null.
            // Android 6.x. data is not null

            Log.d(TAG,"request code is TAKE_VIDEO_ACTION:"  + TAKE_VIDEO_ACTION );
            mVideoUri = data.getData();
            Log.d(TAG, "after TAKE_VIDEO_ACTION mVideoUri is: " + mVideoUri );
            if (mVideoUri != null ) {
              importVideoFromContentUri(mVideoUri);
            }

            if (broadcast)
              broadcastToMediaScanner(mVideoUri);
        }
    }

    static private void importVideo(Intent data) {
        Uri uri = data.getData();

        Log.d(TAG,"importVideo: uri:" + uri);
        Log.d(TAG,"importVideo: type: " + data.getType());

        if (data.getClipData() != null) {
            importVideoFromClipData(data);
        } else if (uri != null ) {
            if (uri.getScheme().equals("file")) {
                importVideoFromFileUri(uri);
            } else {
                importVideoFromContentUri(uri);
            }
        }

    }

    static private void importVideoFromFileUri(Uri uri) {
        ArrayList<Uri> list = new ArrayList(1);
        list.add(uri);
        importVideoFromFileUri(list);
    }

    static private void importVideoFromFileUri(List uris) {
        Map reply = new HashMap();
        ArrayList<String> list = new ArrayList(uris.size());
        ArrayList<String> thumbList = new ArrayList(uris.size());

        Activity activity = org.qtproject.qt5.android.QtNative.activity();
        for (int i = 0 ; i < uris.size() ; i++) {

            Uri uri = Uri.parse( Uri.decode(uris.get(i).toString()));
            String thumbPath = getVideoThumbnail(activity , uri.getPath()  );
            Uri  thumbUri = Uri.fromParts("file", thumbPath ,"");
            thumbList.add(thumbUri.toString());
            list.add(uris.get(i).toString());
        }
        reply.put("videoUrls",list);
        reply.put("videoThumbUrls",thumbList);
        Log.d(TAG , "dispatching to systemdispatcher with CHOSEN_MESSAGE" );
        SystemDispatcher.dispatch(CHOSEN_MESSAGE,reply);
    }


    static private void importVideoFromContentUri(Uri uri) {
        Activity activity = org.qtproject.qt5.android.QtNative.activity();

        String[] columns = { MediaStore.Video.Media.DATA };

        Cursor cursor = activity.getContentResolver().query(uri, columns, null, null, null);
        if (cursor == null) {
            Log.d(TAG,"importVideoFromContentUri: Query failed");
            return;
        }

        cursor.moveToFirst();
        ArrayList<Uri>      uris = new ArrayList(cursor.getCount());

        for (int i = 0 ; i < cursor.getCount(); i++) {
            int columnIndex;
            columnIndex = cursor.getColumnIndex(columns[0]);
            String path = cursor.getString(columnIndex);

            if (path == null) {
                Log.d(TAG,"importVideoFromContentUri: The path of video is null. The video may not on the storage.");
                continue;
            }

            Uri  fileUri = Uri.fromParts("file",path,"");
            uris.add(fileUri);

            Log.d(TAG,"importVideoFromContentUri: " + fileUri.toString());
        }

        importVideoFromFileUri(uris);
        cursor.close();

    }

    public static String getVideoThumbnail(Context context, String filePath ) {

        Log.d(TAG , "getVideoThumbnail called for filePath:" + filePath);
        // first get the VideoId from the path

        String[] columns = {
            MediaStore.Video.Media._ID
        };

        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(
           android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI , columns,
        MediaStore.Video.VideoColumns.DATA + " LIKE ?", new String[] { filePath }, null);
            cursor.moveToFirst();

        if (cursor == null) {
            Log.e(TAG,"query for videoId failed");
            return null;
        }
        cursor.moveToFirst();
        long videoId = cursor.getLong(0);

        Log.d(TAG , "getVideoThumbnail videoId: " + videoId);

         // use videoId to get the thumbnail path
        try {

            String[] projection = {
                    MediaStore.Video.Thumbnails.DATA,
            };
            cursor = cr.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                    new String[] { String.valueOf(videoId) },
                    null);
            if (cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                return cursor.getString(0);
            }

            Log.w(TAG, "Sorry no thumbnail found with video id: " + videoId);
            Log.d(TAG, "Generating new thumbnail." );
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail( filePath , MediaStore.Video.Thumbnails.MICRO_KIND);

            if (bMap != null) {
                 Log.d(TAG, "storing new thumbnail." );
                  storeThumbnail(cr , bMap , videoId , MediaStore.Video.Thumbnails.MICRO_KIND);
            }

            // Query again after storing the Thumbnail in MediaStrore.Video.Thumbnails
            Log.d(TAG, "Querying again for thumbnail" );
            cursor = cr.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                    new String[] { String.valueOf(videoId) },
                    null);

            if (cursor.getCount() > 0 ) {
                Log.d(TAG, "Got thumbnail in second query." );
                cursor.moveToFirst();
                return cursor.getString(0);
            }

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private static void importVideoFromClipData(Intent data) {
        ClipData clipData = data.getClipData();

        Log.d(TAG,"Video importFromClipData");

        if (clipData.getItemCount() == 0)
            return;

        ArrayList<Uri> uris = new ArrayList(clipData.getItemCount());

        for (int i = 0 ; i < clipData.getItemCount() ; i++ ){
            Uri uri = clipData.getItemAt(i).getUri();
            uris.add(resolveUri(uri));
        }
        importVideoFromFileUri(uris);
    }



    static private Uri resolveUri(Uri uri) {
        Activity activity = org.qtproject.qt5.android.QtNative.activity();

        String[] columns = { MediaStore.Video.Media.DATA };

        Cursor cursor = activity.getContentResolver().query(uri, columns, null, null, null);
        if (cursor == null) {
            Log.d(TAG,"Query failed");
            return Uri.parse("");
        }

        cursor.moveToFirst();
        int columnIndex;

        columnIndex = cursor.getColumnIndex(columns[0]);
        String path = cursor.getString(columnIndex);

        cursor.close();
        return Uri.fromParts("file",path,"");
    }

    private static void broadcastToMediaScanner(Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        Activity activity = org.qtproject.qt5.android.QtNative.activity();
        activity.sendBroadcast(mediaScanIntent);
    }

    private static void storeThumbnail(ContentResolver cr, Bitmap thumb, long id, int kind) {

       ContentValues values = new ContentValues(4);
       values.put(MediaStore.Video.Thumbnails.KIND,kind);
       values.put(MediaStore.Video.Thumbnails.VIDEO_ID,(int)id );
       values.put(MediaStore.Video.Thumbnails.HEIGHT,thumb.getHeight());
       values.put(MediaStore.Video.Thumbnails.WIDTH,thumb.getWidth());

       Uri url = cr.insert(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, values);

       try {
           OutputStream thumbOut = cr.openOutputStream(url);
           thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
           thumbOut.close();
       } catch (FileNotFoundException ex) {
           Log.e("IMAGE_COMPRESSION_ERROR", "File not found");
           ex.printStackTrace();
       } catch (IOException ex) {
           Log.e("IMAGE_COMPRESSION_ERROR", "IO Exception");
           ex.printStackTrace();
       }
   }
}
