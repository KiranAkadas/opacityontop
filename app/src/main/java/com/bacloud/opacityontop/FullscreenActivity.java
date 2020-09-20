package com.bacloud.opacityontop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements ColorPickerDialogListener {

    private static final String TAG = "FullscreenActivity";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int DIALOG_ID = 0;
    private static final int REQUEST_OVERLAY_PERMISSION = 1;
    private final Handler mHideHandler = new Handler();
    ColorPickerDialog cc;
    PixelGridView pixelGrid;
    int color;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private TextView mContentText;
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private LinearLayout linearLayout;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */


    private final View.OnClickListener mDelayHideClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            regenerateColorPicker(v);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.fullscreen_content_controls);

        mContentView = findViewById(R.id.fullscreen_content);
        mContentText = findViewById(R.id.fullscreen_content);
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }

        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnClickListener(mDelayHideClickListener);

        Intent intent = new Intent(FullscreenActivity.this, ShakeService.class);
        startService(intent);
//        ScreenshotDetector screenshotDetector =
//                new ScreenshotDetector(this, new ScreenshotDetector.OnScreenshotTakenListener() {
//                    @Override
//                    public void onScreenshotTaken(Uri uri) {
//                        System.out.println("wow"+uri.toString());
//                    }
//                    @Override
//                    public void onScreenshotDeleted(Uri uri) {
//                    }
//                });
//        screenshotDetector.start();
//        if(!Settings.canDrawOverlays(this)){
//            // ask for setting
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
//        }

//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//                PixelFormat.TRANSLUCENT);
//
//        WindowManager wm = (WindowManager) getApplicationContext()
//                .getSystemService(Context.WINDOW_SERVICE);
//
//        ViewGroup mTopView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_fullscreen, null);
//        getWindow().setAttributes(params);
//        wm.addView(mTopView, params);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onColorSelected(int dialogId, int newColor) {
        getSupportFragmentManager().beginTransaction().
                remove(cc).commit();
        this.toggle();
        color = newColor;
        Log.d(TAG, "onColorSelected() called with: dialogId = [" + dialogId + "], color = [" + color + "]");

        linearLayout.removeView(pixelGrid);
        pixelGrid = new PixelGridView(this);
        pixelGrid.setNumColumns(4);
        pixelGrid.setNumRows(6);
        pixelGrid.setColorPicked(color);
        linearLayout.addView(pixelGrid);
        switch (dialogId) {
            case DIALOG_ID:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(FullscreenActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Log.d(TAG, "onDialogDismissed() called with: dialogId = [" + dialogId + "]");
    }

    public void regenerateColorPicker(View view) {

        mContentText.setText("");

        cc = new ColorPickerDialog();
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(DIALOG_ID)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this);

    }

}