package com.example.saolei;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MenuActivity extends AppCompatActivity {
    String t = "easy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    //接收难度界面传回的参数并将其传回主界面处理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            Intent level = new Intent();
            t = data.getStringExtra("result");
            level.putExtra("result", t);
            setResult(1, data);
            finish();
        }
    }

    //点击新游戏按钮，将结果传回主界面
    public void NewGame(View view) {
        Intent data = new Intent();
        data.putExtra("result", "newgame");
        setResult(1, data);
        finish();
    }

    //点击改变难度按钮，启动难度选择界面
    public void ChangeLevel(View view) {
        Intent level = new Intent(MenuActivity.this, LevelActivity.class);
        startActivityForResult(level, 2);
    }

    //点击游戏记录按钮，显示记录
    public void Record(View view) {
        String filename = getExternalCacheDir().getAbsolutePath() + "/gamerecord.txt";//文件路径
        File file = new File(filename);
        Scanner inputStream = null;
        FileInputStream fis = null;
        BufferedReader br = null;
        String str;
        String message = "     " + "级别" + "       " + "胜负" + "         " + "时间\n";
        //若文件不存在，显示暂无记录
        if (!file.exists()) {
            new AlertDialog.Builder(this)
                    .setMessage("暂无记录！")
                    .setNegativeButton("确定", null)
                    .create().show();
        }
        else {
            try {
                //读取文件记录并生成对话框显示
                inputStream = new Scanner(new FileInputStream(filename));
                int i = 1;
                fis = new FileInputStream(filename);
                br = new BufferedReader(new InputStreamReader(fis));
                while ((str = br.readLine()) != null) {
                    message = message + (i + ".  " + str + "\n");
                    i++;
                }
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNegativeButton("确定", null)
                        .create().show();
                fis.close();
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inputStream.close();
            }
        }
    }

    //点击返回按钮，回传结果到主界面
    public void Return1(View view) {
        Intent data = new Intent();
        data.putExtra("result", "start");
        setResult(1, data);
        finish();
    }
}