package com.example.bitcreep.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.bitcreep.R;
import com.example.bitcreep.utils.Constants;
import com.example.bitcreep.utils.PathUtils;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mHolder;

    private ImageView mImageView;
    private VideoView mVideoView;

    private Button mRecordButton;

    private boolean isRecording = false;

    private String mp4Path = "";

    public static void startUI(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SurfaceView mSurfaceView = findViewById(R.id.surfaceview);
        mImageView = findViewById(R.id.iv_img);
        mVideoView = findViewById(R.id.videoview);
        mRecordButton = findViewById(R.id.bt_record);

        mHolder = mSurfaceView.getHolder();
        initCamera();
        mHolder.addCallback(this);
    }

    private void initCamera() {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.set("orientation", "portrait");
        parameters.set("rotation", 90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mp4Path = getOutputMediaPath();
        Log.w(TAG, "prepareVideoRecorder: "+mp4Path);
        mMediaRecorder.setOutputFile(mp4Path);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
//        return mediaFile.getAbsolutePath();
        return "/storage/emulated/0/Pictures/VIDEO_"+timeStamp+".mp4";

    }

    public void takePhoto(View view) {
        mCamera.takePicture(null, null, mPictureCallback);
    }

    //获取照片中的接口回调
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream fos = null;
//            String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "1.jpg";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filePath = "/storage/emulated/0/Pictures/+"+ Constants.USER_NAME+ "_"+timeStamp+".jpg";
            File file = new File(filePath);
            try {
                fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                Bitmap rotateBitmap = PathUtils.rotateImage(bitmap, filePath);
                mImageView.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);
                mImageView.setImageBitmap(rotateBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mCamera.startPreview();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    public void record(View view) {
        if (isRecording) {
            Log.w(TAG, "record: "+mp4Path );
            mRecordButton.setText("录制");

            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();

            mVideoView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mVideoView.setVideoPath(mp4Path);
            mVideoView.start();
        } else {
            if(prepareVideoRecorder()) {
                mRecordButton.setText("停止");
                mMediaRecorder.start();
            }
        }
        isRecording = !isRecording;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        //停止预览效果
        mCamera.stopPreview();
        //重新设置预览效果
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            initCamera();
        }
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }


}