package com.example.bitcreep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
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

        videoView = (FullScreenVideoView) this.findViewById(R.id.fVideoView);
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

//        videoView.setVideoPath(getVideoPath(R.raw.video6));
//        videoView.setVideoURI(Uri.parse("https://sf3-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt41nq/b79c82f2012bfc3d_1614341983449.mp4"));
        final Uri uri = Uri.parse("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4");
        videoView.setVideoURI(uri);
        videoView.requestFocus();
//        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });

    }
    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
}