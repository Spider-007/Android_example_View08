package com.example.android_example_view08;

public interface DownListener {

    void UpdataProgress(long mUpdateProgress);

    void Success();

    void failed();

    void cancel();

    void pause();
}
