package com.example.bitcreep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.MotionEvent;

import com.example.bitcreep.R;
import com.example.bitcreep.utils.Message;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;




public class VideoActivity extends AppCompatActivity {
    private FullScreenVideoView videoView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar()!=null) getSupportActionBar().hide();
        if (getActionBar()!=null) getActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        String studentId = getIntent().getStringExtra("student_id");
        String videoUrl = getIntent().getStringExtra("video_url");

        videoView = (FullScreenVideoView) findViewById(R.id.fVideoView);
        videoView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        videoView.setZOrderOnTop(true);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView.isPlaying()){
                    videoView.pause();
                }else {
                    videoView.start();
                }
                return false;
            }
        });

        videoView.setVideoPath(getVideoPath(R.raw.video6));
        videoView.start();

    }
    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}