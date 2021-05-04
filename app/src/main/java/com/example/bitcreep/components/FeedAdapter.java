package com.example.bitcreep.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;

import com.example.bitcreep.utils.Message;
import com.example.bitcreep.R;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VideoViewHolder>{
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

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView videoCoverSD;
        private TextView userNameTV;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.video_from);
            videoCoverSD = itemView.findViewById(R.id.video_cover);
        }
        public void bind(Message message){
            videoCoverSD.setImageURI(message.getImageUrl());
            userNameTV.setText("FROM: "+message.getUserName());
        }
    }

}

