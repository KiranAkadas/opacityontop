package com.bacloud.opacityontop;


import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;

import java.io.File;

public class ScreenshotDetector extends FileObserver {
    private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots/";
    private boolean deleteScreenshot = false;
    private OnScreenshotTakenListener mListener;
    private String mLastTakenPath;
    private Handler mainThreadHandler;
    public ScreenshotDetector(Context context, OnScreenshotTakenListener listener) {
        super(PATH, FileObserver.CLOSE_WRITE);
        mainThreadHandler = new Handler(context.getMainLooper());
        mListener = listener;
    }

    @Override
    public void onEvent(int event, String path) {
        Log.d(getClass().getSimpleName(), "Event:" + event + "\t" + path);

        if (path == null || event != FileObserver.CLOSE_WRITE)
            Log.d(getClass().getSimpleName(), "Not important");
        else if (mLastTakenPath != null && path.equalsIgnoreCase(mLastTakenPath))
            Log.d(getClass().getSimpleName(), "This event has been observed before.");
        else {
            mLastTakenPath = path;
            final File file = new File(PATH + path);

            if (deleteScreenshot) {
                if (file.exists())
                    file.delete();

                if (mListener != null)
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onScreenshotDeleted(Uri.fromFile(file));
                        }
                    });

            } else {
                if (mListener != null)
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onScreenshotTaken(Uri.fromFile(file));
                        }
                    });
            }
        }
    }

    public void start() {
        super.startWatching();
    }

    public void stop() {
        super.stopWatching();
    }

    public void deleteScreenshot(boolean deleteScreenshot) {
        this.deleteScreenshot = deleteScreenshot;
    }

    public interface OnScreenshotTakenListener {
        void onScreenshotTaken(Uri uri);

        void onScreenshotDeleted(Uri uri);
    }

}