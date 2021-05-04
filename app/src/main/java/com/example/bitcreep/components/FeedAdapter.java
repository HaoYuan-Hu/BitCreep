package com.example.bitcreep.components;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitcreep.activity.VideoActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import com.example.bitcreep.utils.Message;
import com.example.bitcreep.R;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<VideoViewHolder>{
    private List<Message> data;
    public void setData(List<Message> messageList){
        data = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false);
        return new VideoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}

