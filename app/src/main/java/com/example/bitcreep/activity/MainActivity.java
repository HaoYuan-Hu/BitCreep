package com.example.bitcreep.activity;


import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import com.example.bitcreep.R;
import com.example.bitcreep.components.FeedAdapter;
import com.example.bitcreep.utils.Constants;
import com.example.bitcreep.utils.IApi;
import com.example.bitcreep.utils.Message;
import com.example.bitcreep.utils.MessageListResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final static int PERMISSION_REQUEST_CODE = 1001;

    private FeedAdapter adapter = new FeedAdapter();

    private IApi api;
    private Retrofit retrofit;

    // 创建时初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化网络接口
        initNetwork();

        // 设置滚动窗口相关
        RecyclerView recyclerView = findViewById(R.id.video_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 设置按钮相关
        findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCamera();
            }
        });
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessagesFromRemote(Constants.STUDENT_ID);
            }
        });
        findViewById(R.id.getall_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessagesFromRemote(null);
            }
        });
    }

    // 初始化网络接口
    private void initNetwork() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(IApi.class);
    }

//    // 开设新的线程，获取远端数据
//    private void getData(String studentId){
//        Log.d(TAG,"getData start");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getMessagesFromRemote(studentId);
//            }
//        }).start();
//    }

    // 通过 IApi 获取远端数据的逻辑，同时设置远端数据
    public void getMessagesFromRemote(String studentId){
        Log.d(TAG,"get remote messages!");
        Call<MessageListResponse> response = api.getVideos(studentId);
        Log.d(TAG,"finish call create");

        response.enqueue(new Callback<MessageListResponse>() {
            @Override
            public void onResponse(final Call<MessageListResponse> call, final Response<MessageListResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG,"response is not successful");
                    Toast.makeText(MainActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG,"response is successful");
                    Toast.makeText(MainActivity.this,"获取成功",Toast.LENGTH_SHORT).show();

                    MessageListResponse messagesListRes = response.body();
//
//                    if (messagesListRes != null && !messagesListRes.feeds.isEmpty()){
//                        new Handler(getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d(TAG,"Show messages" + messagesListRes.feeds.size());
//                                Toast.makeText(MainActivity.this, "共找到" + messagesListRes.feeds.size() + "条结果", Toast.LENGTH_SHORT).show();
//                                adapter.setData(messagesListRes.feeds);
//                            }
//                        });
//                    }
//                    else {
//                        Log.d(TAG,"data result can be null!");
//                        Toast.makeText(MainActivity.this, "返回结果为空", Toast.LENGTH_SHORT).show();
//                    }
                }
            }

            @Override
            public void onFailure(final Call<MessageListResponse> call, final Throwable t) {
                Log.d(TAG,"Main Activity" + "获取失败");
                t.printStackTrace();
                Toast.makeText(MainActivity.this,"onFailure",Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG,"function over");
    }


//    // 如果当前 Activity 中有子 Activity 结束，则会触发这个函数
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
//            if (resultCode == Activity.RESULT_OK) {
//                coverImageUri = data.getData();
//                coverSD.setImageURI(coverImageUri);
//
//                if (coverImageUri != null) {
//                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
//                } else {
//                    Log.d(TAG, "uri2File fail " + data.getData());
//                }
//
//            } else {
//                Log.d(TAG, "file pick fail");
//            }
//        }
//    }

    public void customCamera() {
        requestPermission();
    }

    private void recordVideo() {
        CameraActivity.startUI(this);
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }
}