package com.example.android_example_view08;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int WRITE_PERMISSION_CODE = 100;
    private Button mDownLoadButton, mPauseButton, mCancelButton;
    private MyDownLoadService.MyBinder binder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MyDownLoadService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mDownLoadButton = findViewById(R.id.downLoadBtn);
        mPauseButton = findViewById(R.id.pauseBtn);
        mCancelButton = findViewById(R.id.cancelBtn);
        mDownLoadButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        //start handler
        Intent mIntent = new Intent(this, MyDownLoadService.class);
        startService(mIntent);
        bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);

        //user 4.3add write permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downLoadBtn:
//                String mString = "5652%2F098c%2F78f2%2Fe944028f6b3821da471aeae0e46766b5.mp3";
                String mString = "http://m7.music.126.net/20191007211444/cec9524e0a31049be278abfda61a0d4b/ymusic/5652/098c/78f2/e944028f6b3821da471aeae0e46766b5.mp3";
                binder.startBinder(mString);
                break;
            case R.id.pauseBtn:
                binder.pauseBinder();
                break;
            case R.id.cancelBtn:
                binder.cancelBinder();
                break;
            default:
                throw new NullPointerException("Null id");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults.length >= 0) {
                    Toast.makeText(this, "Permission defined", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
