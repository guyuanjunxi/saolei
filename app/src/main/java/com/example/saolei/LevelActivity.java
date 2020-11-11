package com.example.saolei;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
    }

    /*根据不同难度返回相应参数*/
    public void Easy(View view) {
        Intent data = new Intent();
        data.putExtra("result", "easy");
        setResult(2, data);
        finish();
    }

    public void Hard(View view) {
        Intent data = new Intent();
        data.putExtra("result", "hard");
        setResult(2, data);
        finish();
    }

    public void Return(View view) {
        Intent data = new Intent();
        data.putExtra("result", "");
        setResult(2, data);
        finish();
    }
}