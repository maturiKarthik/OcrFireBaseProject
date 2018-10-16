package com.example.maturi.cardscannew;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.images.Size;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private  Context mContext;
    private static final float FOCUS_AREA_SIZE = 75f;

    private static final int JPEG_QUALITY = 100;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mContext = context;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {

            mCamera.setPreviewDisplay(holder);


            /**
             * Camera Parameters
             */

            Camera.Parameters parameters = mCamera.getParameters();
          //  parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            /**
             * For Better Preview
             * Get Parameters
             *  parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
             */
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setJpegQuality(JPEG_QUALITY);

            List<Camera.Size> sizes=parameters.getSupportedPictureSizes();
            parameters.setPictureSize(sizes.get(0).width, sizes.get(0).height);
            mCamera.setParameters(parameters);
            // Start Preview
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private Rect createAutoFocusRect() {
        int left = (int) (getWidth() / 2 - FOCUS_AREA_SIZE);
        int right = (int) (getWidth() / 2 + FOCUS_AREA_SIZE);
        int top = (int) (getHeight() / 2 - FOCUS_AREA_SIZE);
        int bottom = (int) (getHeight() / 2 + FOCUS_AREA_SIZE);
        return new Rect(left, top, right, bottom);
    }
}