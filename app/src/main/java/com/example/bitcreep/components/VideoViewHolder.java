package com.example.bitcreep.components;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitcreep.R;
import com.example.bitcreep.activity.MainActivity;
import com.example.bitcreep.activity.VideoActivity;
import com.example.bitcreep.utils.Message;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private String TAG = "VideoViewHolder";

    /* 要展示的组件 */
    private TextView userNameTV; // 发布者姓名
    private SimpleDraweeView videoCoverSD; // 视频封面
    private TextView createTimeTV; // 拍摄时间
    private TextView updateTimeTV; // 上传时间

    /* 要储存的组件 */
    private String userId; // 发布者Id
    private String userStudentId; // 发布者学号
    private String videoUrl; // 视频 URL
    private int imageW; // 封面宽度
    private int imageH; // 封面高度

    public VideoViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameTV = itemView.findViewById(R.id.video_userName);
        videoCoverSD = itemView.findViewById(R.id.video_cover);
        createTimeTV = itemView.findViewById(R.id.video_createTime);
        updateTimeTV = itemView.findViewById(R.id.video_updateTime);
        itemView.setOnClickListener(this);
    }

    public void bind(Message message){
        String imageUrlTmp = message.getImageUrl();
        String userNameTmp = message.getUserName();
        String createdAtTmp = message.getCreatedAt();
        String updatedAtTmp = message.getUpdatedAt();

        // 绑定显示的组件
        videoCoverSD.setImageURI(imageUrlTmp);
        userNameTV.setText("Biter："+ userNameTmp);
        createTimeTV.setText("拍摄：" + createdAtTmp.substring(0, 4) + "年" + createdAtTmp.substring(5,7) + "月" + createdAtTmp.substring(8,10) + "日");
        updateTimeTV.setText("上传：" + updatedAtTmp.substring(0, 4) + "年" + updatedAtTmp.substring(5,7) + "月" + updatedAtTmp.substring(8,10) + "日");

        // 绑定不显示的数据
        userId = message.getId();
        userStudentId = message.getStudentId();
        videoUrl = message.getVideoUrl();
        imageW = message.getImageW();
        imageH = message.getImageH();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"go to VideoActivity with URL " + videoUrl);
        Intent intent = new Intent(v.getContext(), VideoActivity.class);

        intent.putExtra("student_id", userStudentId);
        intent.putExtra("user_name", userNameTV.getText().toString());
        intent.putExtra("video_url", videoUrl);
        intent.putExtra("createdTime", createTimeTV.getText().toString());

        v.getContext().startActivity(intent);
    }
}
