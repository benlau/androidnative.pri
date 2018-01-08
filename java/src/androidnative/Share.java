package androidnative;

import android.app.Activity;
import android.os.Build;
import android.content.Intent;
import android.util.Log;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException ;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory ;
import android.provider.MediaStore;
import android.provider.MediaStore.Files ;



import org.qtproject.qt5.android.QtNative;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.net.URL ;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream ;


import java.net.MalformedURLException;
import java.io.IOException;


public class Share {

    private static final String TAG = "androidnative.Share";

    public static final String SHARE_CONTENT = "androidnative.Util.shareContent";
    private static final String SHARE_FILE_VOLUME_NAME = "external";


    static {
        SystemDispatcher.addListener(new SystemDispatcher.Listener() {
            public void onDispatched(String type, Map message) {
                if (type.equals(SHARE_CONTENT)) {
                    shareContent(message);
                }
            }
        });
    }

    static void shareContent (Map message) {

        Intent shareIntent ;
        Activity activity = org.qtproject.qt5.android.QtNative.activity();

        boolean multiple = false;

        if ( message.containsKey("uris")
         ||  message.containsKey("urls")
         ||  message.containsKey("texts")

         ) {
            multiple = true;
        }

        if (multiple) {
            shareIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
            } else {
            shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        String mimeType = (String)  message.get("mime_type");
        shareIntent.setType( mimeType);


        if ( message.containsKey("uris")) {

            ArrayList<String> uris = (ArrayList<String>) message.get("uris") ;
            ArrayList<Uri> Uris = new ArrayList<Uri>();
            for (int i = 0 ; i < uris.size() ; i++) {
                Uris.add ( Uri.parse( Uri.decode( uris.get(i).toString() ) ) );
            }
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uris);
        }

        if ( message.containsKey("uri")) {
            String uriString  = (String) message.get("uri") ;
            String decoded = Uri.decode(uriString);
            Uri uri =  Uri.parse( decoded );
            shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri );
        }

        if ( message.containsKey("urls")) {

            ArrayList<String> urls = (ArrayList<String>) message.get("urls") ;
            ArrayList<Uri> Uris = new ArrayList<Uri>();

            for (int i = 0 ; i < urls.size() ; i++) {
                String urlString = urls.get(i).toString() ;
                Uri uri = saveUrl (urlString);
                Uris.add ( uri );
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uris);
        }


        if ( message.containsKey("url")) {
            String urlString  = (String) message.get("url") ;
            Uri  uri = saveUrl (urlString);
            shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri );
        }

        if ( message.containsKey("texts")) {
            ArrayList<String> texts = (ArrayList<String>) message.get("texts") ;
            shareIntent.putExtra(Intent.EXTRA_TEXT , texts );
        }


        if ( message.containsKey("text")) {
            String text  = (String) message.get("text") ;
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text );
        }

        if ( message.containsKey("html_texts")) {
            ArrayList<String> html_texts = (ArrayList<String>) message.get("html_texts") ;
            shareIntent.putExtra(Intent.EXTRA_HTML_TEXT , html_texts );
        }

        if (message.containsKey("html_text") ) {
            String html_text  = (String) message.get("html_text");
            shareIntent.putExtra(android.content.Intent.EXTRA_HTML_TEXT, html_text );
        }

        if (message.containsKey("subject") ) {
            String subject  = (String) message.get("subject");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject );
        }

        if (message.containsKey("email") ) {
            String email  = (String) message.get("email");
            shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email );
        }

        if (message.containsKey("cc") ) {
            String cc  = (String) message.get("cc");
            shareIntent.putExtra(android.content.Intent.EXTRA_CC, cc );
        }

        if (message.containsKey("bcc") ) {
            String bcc  = (String) message.get("bcc");
            shareIntent.putExtra(android.content.Intent.EXTRA_BCC, bcc );
        }



        if (message.containsKey("package") ) {
            String packageName  = (String) message.get("package");
            shareIntent.setPackage( packageName);
            activity.startActivity(shareIntent);
            try {
                activity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e(TAG , " specified package " + packageName + " is not installed ");
            }

        } else {
            if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
                String title  = (String) message.get("title");
                activity.startActivity(Intent.createChooser(shareIntent, title ));
             } else {
                Log.e(TAG , "sorry not application found to handle the content being shared.");
            }
        }
    }

    static Uri saveUrl (String urlString) {

            URL url;
            Activity activity = org.qtproject.qt5.android.QtNative.activity();

            File baseDir=activity.getExternalFilesDir(null);
            String fileName = Uri.parse(Uri.decode(urlString)).getLastPathSegment();

            File localFile = null ;

            try {
                url = new URL(urlString);
                InputStream in = new BufferedInputStream(url.openStream());
                localFile = new File(baseDir , fileName );
                FileOutputStream fos = new FileOutputStream(localFile);
                byte[] buf = new byte[8192];
                int len = 0;
                while ((len = in.read(buf)) != -1) {
                    fos.write(buf,0,len);
                }
                in.close();
                fos.close();

                } catch (IOException e)  {
                    e.printStackTrace();
            }
            String localFileName = localFile.getAbsolutePath() ;

            // convert to file uri
            Uri uri = Uri.parse( "file:" + localFileName );

            return uri ;
    }

}
