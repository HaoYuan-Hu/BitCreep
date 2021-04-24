package com.example.bitcreep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PixelFormat;
import android.os.Bundle;

import com.example.bitcreep.R;
import com.example.bitcreep.utils.Message;

import android.widget.VideoView;



public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        String studentId = getIntent().getStringExtra("student_id");
        String videoUrl = getIntent().getStringExtra("video_url");

        videoView = findViewById(R.id.videoView);
        videoView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        videoView.setZOrderOnTop(true);

        videoView.setVideoPath(getVideoPath(R.raw.video1));
    }
    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}