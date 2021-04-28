package com.example.bitcreep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class VideoActivity extends AppCompatActivity {
    private FullScreenVideoView videoView;
    private TextView textView;
    private TextView timeView;
    private SeekBar seekBar;
    private TextView textViewTime;
    private TextView textViewCurrentPosition;
    private LottieAnimationView animationView;
    private ObjectAnimator playDisappear;
    private ObjectAnimator playAppear;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                seekBar.setProgress(current);
                textViewCurrentPosition.setText(time(videoView.getCurrentPosition()));
            }
            handler.postDelayed(runnable, 500);
        }
    };

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
//        String videoUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
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

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        textViewCurrentPosition = (TextView)findViewById(R.id.textViewCurrentPosition);
        textViewTime = (TextView)findViewById(R.id.textViewTime);

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
                    seekBar.setVisibility(View.VISIBLE);
                    textViewCurrentPosition.setVisibility(View.VISIBLE);
                    textViewTime.setVisibility(View.VISIBLE);
                }else {
                    videoView.start();
                    playDisappear.start();
                    seekBar.setVisibility(View.INVISIBLE);
                    textViewCurrentPosition.setVisibility(View.INVISIBLE);
                    textViewTime.setVisibility(View.INVISIBLE);
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
                // 开始线程，更新进度条的刻度
                handler.postDelayed(runnable, 0);
                videoView.start();
                textViewTime.setText(time(videoView.getDuration()));
                seekBar.setMax(videoView.getDuration());
                seekBar.setVisibility(View.INVISIBLE);
                textViewCurrentPosition.setVisibility(View.INVISIBLE);
                textViewTime.setText(time(videoView.getDuration()));
                textViewTime.setVisibility(View.INVISIBLE);
            }
        });

    }
    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }
    protected String time(long millionSeconds) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            videoView.seekTo(progress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}