package com.example.bitcreep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitcreep.R;
import com.example.bitcreep.utils.Constants;
import com.facebook.drawee.backends.pipeline.Fresco;

public class ChangeInfoActivity extends AppCompatActivity {

    // 创建时初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
    }


}
