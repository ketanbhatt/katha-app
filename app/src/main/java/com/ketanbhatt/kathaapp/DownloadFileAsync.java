package com.ketanbhatt.kathaapp;

/**
 * Created by ktbt on 05/07/17.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadFileAsync extends AsyncTask<String, String, String> {

    private static final String TAG ="DOWNLOADFILE";

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private PostDownload callback;
    private Context context;
    private FileDescriptor fd;
    private File file;
    private String downloadLocation;

    private Integer notif_id = 1;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    public DownloadFileAsync(String downloadLocation, Context context, PostDownload callback){
        this.context = context;
        this.callback = callback;
        this.downloadLocation = downloadLocation;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNotifyManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this.context);
        mBuilder.setContentTitle("Downloading Book")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL(aurl[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lenghtOfFile = connection.getContentLength();
            Log.d(TAG, "Length of the file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            file = new File(downloadLocation);
            FileOutputStream output = new FileOutputStream(file); //context.openFileOutput("content.zip", Context.MODE_PRIVATE);
            Log.d(TAG, "file saved at " + file.getAbsolutePath());
            fd = output.getFD();

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {}
        return null;

    }
    protected void onProgressUpdate(String... progress) {
        Log.d(TAG, progress[0]);
        mBuilder.setProgress(100, Integer.parseInt(progress[0]), false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(notif_id, mBuilder.build());
    }

    @Override
    protected void onPostExecute(String unused) {
        mBuilder.setContentTitle("Book Downloaded")
                .setContentText("Download Complete");

        mNotifyManager.notify(notif_id, mBuilder.build());

        if(callback != null) callback.downloadDone(file);
    }

    public static interface PostDownload{
        void downloadDone(File fd);
    }
}
