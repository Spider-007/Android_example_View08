package com.example.android_example_view08;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;

public class MyDownLoadService extends Service {

    private DownAsyncTask mDownAsyncTask;
    private String downLoadUrl;

    private DownListener mDownListener = new DownListener() {
        @Override
        public void UpdataProgress(long mUpdateProgress) {
            mNotificationManager().notify(1, getNotifications("UpdateProgress", (int) mUpdateProgress));
        }

        @Override
        public void Success() {
            mDownAsyncTask = null;
            stopForeground(true);
            mNotificationManager().notify(1, getNotifications("Success", -1));
            Toast.makeText(MyDownLoadService.this, "DownLoad Success ", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void failed() {
            mDownAsyncTask = null;
            stopForeground(true);
            mNotificationManager().notify(1, getNotifications("failed", -1));
            Toast.makeText(MyDownLoadService.this, "DownLoad failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void cancel() {
            mDownAsyncTask = null;
            stopForeground(true);
            Toast.makeText(MyDownLoadService.this, "Download cancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void pause() {
            mDownAsyncTask = null;
            Toast.makeText(MyDownLoadService.this, "Download Pause", Toast.LENGTH_SHORT).show();
        }
    };

    private NotificationManager mNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    private MyBinder mMyBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMyBinder;
    }

    public class MyBinder extends Binder {

        public void startBinder(String targetAddress) {
            if (mDownAsyncTask == null) {
                downLoadUrl = targetAddress;
                mDownAsyncTask = new DownAsyncTask(mDownListener);
                mDownAsyncTask.execute(targetAddress);
                startForeground(1, getNotifications("Downloading", 0));
                Toast.makeText(MyDownLoadService.this, "Downloading", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseBinder() {
            if (mDownAsyncTask != null) {
                mDownAsyncTask.isPause();
            }
        }

        public void cancelBinder() {
            if (mDownAsyncTask != null) {
                mDownAsyncTask.isCanle();
            }
            if (mDownAsyncTask != null) {
                String fileName = downLoadUrl.substring(downLoadUrl.lastIndexOf("/"));
                String mfilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File mFile = new File(mfilePath + fileName);
                if (mFile.exists()) {
                    mFile.delete();
                }
                mNotificationManager().cancel(0);
                Toast.makeText(MyDownLoadService.this, "Cancel DownLoad", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Notification getNotifications(String title, int progress) {
        Intent mIntent = new Intent(this, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        NotificationCompat.Builder mNotificationCompat = new NotificationCompat.Builder(this).setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(mPendingIntent);
        if (progress >= 0) {
            mNotificationCompat.setContentText(progress + "%")
                    .setProgress(100, progress, false);
        }
        return mNotificationCompat.build();
    }
}
