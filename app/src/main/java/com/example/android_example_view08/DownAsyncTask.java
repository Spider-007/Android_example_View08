package com.example.android_example_view08;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownAsyncTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_PAUSE = 3;
    public static final int TYPE_CANLE = 4;

    private int lastProgress;
    private boolean isCanle = false;
    private boolean isPause = false;
    private DownListener mDownListener;

    public DownAsyncTask(DownListener mDownListener) {
        this.mDownListener = mDownListener;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case TYPE_SUCCESS:
                mDownListener.Success();
                break;
            case TYPE_FAILED:
                mDownListener.failed();
                break;
            case TYPE_CANLE:
                mDownListener.cancel();
                break;
            case TYPE_PAUSE:
                mDownListener.pause();
                break;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int mProgress = values[0];
        if (mProgress > lastProgress) {
            mDownListener.UpdataProgress(mProgress);
            lastProgress = mProgress;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected Integer doInBackground(String... strings) {

        InputStream mInputStream = null;
        RandomAccessFile mRandomAccessFile = null;
        File mFile = null;
        try {
            //init File
            String downloadUrl = strings[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            long downLoadLength = downloadUrl.length();
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            mFile = new File(filePath + fileName);
            if (mFile.exists()) {
                downLoadLength = mFile.length();
            }
            //decide file length
            long requestdownLoadLength = getCurrentRequestAccessLength(downloadUrl);
            if (requestdownLoadLength == 0) {
                return TYPE_FAILED;
            } else if (requestdownLoadLength == downLoadLength) {
                return TYPE_SUCCESS;
            }
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request mRequest = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downLoadLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = mOkHttpClient.newCall(mRequest).execute();
            if (response != null) {
                mInputStream = response.body().byteStream();
                mRandomAccessFile = new RandomAccessFile(mFile, "rw");
                mRandomAccessFile.seek(downLoadLength); //跳过下载路径 下次再打开直接继续
                int mLength;
                int currentLength = 0;
                byte[] mbyte = new byte[1024];
                //warning byte里面包含0 和 1判断尽量写为 -1
                while ((mLength = mInputStream.read(mbyte)) != -1) {
                    if (isCanle) {
                        return TYPE_CANLE;
                    } else if (isPause) {
                        return TYPE_PAUSE;
                    } else
                        currentLength += mLength;
                    mRandomAccessFile.write(mbyte, 0, currentLength);
                    int progressLength = (int) ((currentLength + downLoadLength) * 100 / requestdownLoadLength);
                    publishProgress(progressLength);
                }
            }
            response.body().close();
            return TYPE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mFile != null) {
                mFile.delete();
            }
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                    if (mRandomAccessFile != null) {
                        mRandomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return TYPE_FAILED;
    }


    public void isCanle() {
        isCanle = true;
    }

    public void isPause() {
        isPause = true;
    }

    private long getCurrentRequestAccessLength(String mString) {

        long requestLength;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request mRequest = new Request.Builder()
                .url(mString).build();
        try {
            Response mResponse = mOkHttpClient.newCall(mRequest).execute();
            if (mResponse != null && mResponse.body().contentLength() != 0) {
                requestLength = mResponse.body().contentLength();
                return requestLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }
        return 0;
    }
}
