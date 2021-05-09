package com.example.bitcreep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import com.example.bitcreep.R;
import com.example.bitcreep.utils.Constants;
import com.example.bitcreep.utils.IApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import com.example.bitcreep.utils.UploadResponse;
import com.example.bitcreep.utils.Util;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_VIDEO = 102;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final String VIDEO_TYPE = "video/*";

    private IApi api;
    private Uri coverImageUri;
    private Uri videoUri;
    private SimpleDraweeView coverSD;
    private VideoView videoSD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        Fresco.initialize(this);
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        videoSD = findViewById(R.id.sd_video);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, COVER_IMAGE_TYPE, "选择视频");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }
            } else {
                Log.d(TAG, "file pick fail");
            }
        }
        else if(REQUEST_CODE_VIDEO == requestCode)
        {
            if (resultCode == Activity.RESULT_OK) {
                videoUri = data.getData();
                videoSD.setVideoURI(videoUri);
                videoSD.start();
            }
            else {
                Log.d(TAG, "video pick fail");
            }

//            String[] filePathColumn = { MediaStore.Video.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedVideo ,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String videoPath = cursor.getString(columnIndex);
//
//            MediaMetadataRetriever media = new MediaMetadataRetriever();
//
//            media.setDataSource(videoPath);
//
//            media.getFrameAtTime();

        }
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void initNetwork() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private void submit() {

        Log.d(TAG, "submit: start");
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part image = MultipartBody.Part.createFormData("cover_image", "cover.png", RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));

        byte[] videoData = readDataFromUri(videoUri);
        MultipartBody.Part video = MultipartBody.Part.createFormData("video","video.mp4",RequestBody.create(MediaType.parse("multipart/form-data"), videoData));
        Call<UploadResponse> response = api.uploadVideo(Constants.STUDENT_ID, Constants.USER_NAME,
                "测试", image, video);

        response.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(final Call<UploadResponse> call, final Response<UploadResponse> response) {
                Log.d(TAG, "onResponse: ");
                System.out.print(response);
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //todo
                            Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
 
            @Override
            public void onFailure(final Call<UploadResponse> call, final Throwable t) {
                t.printStackTrace();
            }
        });
    }
}