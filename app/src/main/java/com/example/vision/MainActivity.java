package com.example.vision;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    private CameraBridgeViewBase cameraView;
    private Mat rgba, gray;

    private String dTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dTag = getResources().getString(R.string.log_debug);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraView = findViewById(R.id.cameraView);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(cameraViewListener);
    }

    @Override
    protected void onResume() {

        super.onResume();
        setupCamera();
        openCamera();
    }

    @Override
    protected void onPause() {

        super.onPause();

        if(cameraView != null) {

            cameraView.disableView();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            // Enables regular immersive mode (SYSTEM_UI_FLAG_IMMERSIVE).
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive", replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void setupCamera() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, 1);
        }
    }

    private void openCamera() {

        if(OpenCVLoader.initDebug()) {

            Log.d(dTag, "Initialization of OpenCV was successful");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else {

            Log.d(dTag, "initialization of OpenCV was unsuccessful");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,
                    this, baseLoaderCallback);
        }
    }

    CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener = new CameraBridgeViewBase.CvCameraViewListener2() {

        @Override
        public void onCameraViewStarted(int width, int height) {

            rgba = new Mat(width, height, CvType.CV_8UC4);
            gray = new Mat(width, height, CvType.CV_8UC4);
        }

        @Override
        public void onCameraViewStopped() {

            rgba.release();
            gray.release();
            //mat.release();
        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

            rgba = inputFrame.rgba();
            //gray = inputFrame.gray();
            Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY);

            return gray;
        }
    };

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {

            if (status == BaseLoaderCallback.SUCCESS) {

                cameraView.enableView();
            }
            else { super.onManagerConnected(status); }
        }
    };
}
