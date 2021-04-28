package com.example.bitcreep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;





public class VideoActivity extends AppCompatActivity {
    private FullScreenVideoView videoView;
    private TextView textView;
    private TextView timeView;
    private SeekBar seekBar;
    private LottieAnimationView animationView;
    private ObjectAnimator playDisappear;
    private ObjectAnimator playAppear;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar()!=null) getSupportActionBar().hide();
        if (getActionBar()!=null) getActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        String studentId = getIntent().getStringExtra("student_id");
//        String userName = getIntent().getStringExtra("user_name");
//        String videoUrl = getIntent().getStringExtra("video_url");
//        String createTime = getIntent().getStringExtra("createdTime");
        String studentId = "518021910095";
        String userName = "Ke";
        //String videoUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
//        String videoUrl = "https://sf3-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt41nq/b79c82f2012bfc3d_1614341983449.mp4";
        String videoUrl = "https://sf3-hscdn-tos.pstatp.com/obj/developer-baas/baas/tt41nq/41be9c2b1bc480ba_1619510654875.mp4";
        String createTime = "2021-04-27T08:04:14.951Z";

        textView = this.findViewById(R.id.text_view);
        textView.setText("@" + userName + " (" + studentId + ")");
        timeView = this.findViewById(R.id.time_view);
        timeView.setText(" · " + createTime.substring(0, 4) + "年"+createTime.substring(5,7) + "月"+createTime.substring(8,10) + "日");

        animationView = this.findViewById(R.id.animation_view);
        animationView.bringToFront();
        animationView.setVisibility(View.INVISIBLE);

        playDisappear = ObjectAnimator.ofFloat(animationView,
                "alpha", 1.0f, 0.0f);
        playAppear = ObjectAnimator.ofFloat(animationView,
                "alpha", 0.0f, 1.0f);

        videoView = (FullScreenVideoView) this.findViewById(R.id.fVideoView);
        videoView.getHolder().setFormat(PixelFormat.TRANSPARENT);
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
                    playAppear.start();
                    animationView.playAnimation();
                    animationView.bringToFront();
                    animationView.setVisibility(View.VISIBLE);
                }else {
                    videoView.start();
                    playDisappear.start();
                }
                return false;
            }
        });

        final Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);
//        videoView.setVideoPath(getVideoPath(R.raw.video5));
        videoView.requestFocus();
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