package com.ketanbhatt.kathaapp;

/**
 * Created by ktbt on 05/07/17.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** package **/
class DownloadFileAsync extends AsyncTask<String, Integer, File> {

    private static final String TAG = DownloadFileAsync.class.getSimpleName();

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private PostDownload callback;
    private Context context;
    private String downloadLocation;
    private Integer mNotifId = 1;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    DownloadFileAsync(String downloadLocation, Context context, PostDownload callback) {
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
    protected File doInBackground(String... aurl) {
        int count;

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            URL url = new URL(aurl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            int lengthOfFile = connection.getContentLength();
            Log.d(TAG, "Length of the file: " + lengthOfFile);

            inputStream = new BufferedInputStream(url.openStream());
            File file = new File(downloadLocation);
            fileOutputStream = new FileOutputStream(file);
            Log.d(TAG, "file saved at " + file.getAbsolutePath());

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = inputStream.read(data)) != -1) {
                total += count;

                Integer progress = (int) ((total * 100) / lengthOfFile);
                if (progress % 5 == 0) {
                    publishProgress(progress);
                }

                fileOutputStream.write(data, 0, count);
            }
            fileOutputStream.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        Log.d(TAG, String.valueOf(progress[0]));
        mBuilder.setProgress(100, progress[0], false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(mNotifId, mBuilder.build());
    }

    @Override
    protected void onPostExecute(File downloadedFile) {
        super.onPostExecute(downloadedFile);
        mNotifyManager.cancel(mNotifId);
        if (downloadedFile != null) {
            mBuilder.setContentTitle("Book Downloaded")
                    .setContentText("Downloading Complete")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setProgress(100, 100, false);
            if (callback != null)
                callback.onDownloadComplete(downloadedFile);
        } else {
            mBuilder.setContentTitle("Downloading Failed")
                    .setContentText("Downloading Failed")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setProgress(100, 100, false);
            if (callback != null)
                callback.onDownloadFailure();
        }
        mNotifyManager.notify(mNotifId, mBuilder.build());
    }

    interface PostDownload {
        void onDownloadComplete(File fd);

        void onDownloadFailure();
    }
}
