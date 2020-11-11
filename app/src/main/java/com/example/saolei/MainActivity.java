package com.example.saolei;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public boolean flagging = false;//是否标记
    public boolean isGameOver = false;
    private GridLayout MineField;
    private TextView txtMineCount;
    private Button openbtn, flagbtn;
    private String time = "00:00:00";
    private TextView txtTimer;
    private String Level = "Easy";//游戏难度
    private Handler timer = new Handler();
    private int Seconds = 0;
    public boolean isTimerStarted = false;
    public int numberOfRowsInMineField = 9;
    public int numberOfColumnsInMineField = 9;
    public int totalNumberOfMines = 10;
    private int Mines = 10;//初始雷数
    private int Opened = 0;//目前已翻开个数
    private int Flagged = 0;//目前被标记个数
    private Block[][] blocks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MineField = (GridLayout) findViewById(R.id.mines);
        txtMineCount = (TextView) findViewById(R.id.minecount);
        txtTimer = (TextView) findViewById(R.id.time1);
        openbtn = (Button) findViewById(R.id.open);
        flagbtn = (Button) findViewById(R.id.flag);
        init();
    }

    @Override
    //接收菜单界面传回的参数进行处理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //点击新游戏按钮进行初始化
        if (resultCode == 1) {
            if (data.getStringExtra("result").equals("newgame")) {
                init();
            }
            //根据难度更新难度和总雷数，重新开始游戏
            else if (data.getStringExtra("result").equals("easy")) {
                numberOfColumnsInMineField = numberOfRowsInMineField = 9;
                totalNumberOfMines = 10;
                Level = "Easy";
                init();
            } else if (data.getStringExtra("result").equals("hard")) {
                numberOfColumnsInMineField = numberOfRowsInMineField = 12;
                totalNumberOfMines = 30;
                Level = "Hard";
                init();
            }
            //恢复暂停的计时器
            else {
                if (!isGameOver && isTimerStarted) {
                    startTimer();
                }
            }
        }
    }

    //调用菜单界面
    public void Menu(View view) {
        Intent menu = new Intent(MainActivity.this, MenuActivity.class);
        startActivityForResult(menu, 1);
        stopTimer();//点击菜单按钮暂停计时器
    }

    //游戏初始化
    public void init() {
        MineField.removeAllViews();
        Seconds = 0;
        Mines = totalNumberOfMines;
        isGameOver = false;
        Flagged = 0;
        isTimerStarted = false;
        flagging = false;
        time = "00:00:00";
        Opened = 0;
        stopTimer();
        txtMineCount.setText(Integer.toString(totalNumberOfMines));
        txtTimer.setText(time);
        openbtn.setEnabled(false);//初始将翻开按钮置为不可点击
        flagbtn.setEnabled(true);
        openbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagging = false;
                flagbtn.setEnabled(true);
                openbtn.setEnabled(false);
            }
        });
        flagbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagging = true;
                flagbtn.setEnabled(false);
                openbtn.setEnabled(true);
            }
        });
        createMineField();
        showMineField();
    }

    //创建雷区
    private void createMineField() {
        blocks = new Block[numberOfRowsInMineField][numberOfColumnsInMineField];
        for (int row = 0; row < numberOfRowsInMineField; row++) {
            for (int column = 0; column < numberOfColumnsInMineField; column++) {
                blocks[row][column] = new Block(this);
                blocks[row][column].setDefaults();
                final int currentRow = row;
                final int currentColumn = column;
                final Block temp = blocks[currentRow][currentColumn];
                blocks[row][column].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //若计时器未启动，启动计时器
                        if (!isTimerStarted) {
                            startTimer();
                            isTimerStarted = true;
                        }
                        //翻开
                        if (!flagging) {
                            if (!temp.isFlagged()) {
                                // 翻开自己及周围的雷块
                                rippleUncover(currentRow, currentColumn);
                                //若触雷则游戏结束
                                if (temp.hasMine()) {
                                    finishGame();
                                }
                            }
                        }
                        //标记
                        else {
                            if (temp.isCovered()) {
                                if (temp.isFlagged()) {
                                    blocks[currentRow][currentColumn].clearAllIcons();
                                    blocks[currentRow][currentColumn].setFlagged(false);
                                    UpdateMinecount(true);//更新计数器
                                } else {
                                    blocks[currentRow][currentColumn].setFlagIcon(false);
                                    blocks[currentRow][currentColumn].setFlagged(true);
                                    UpdateMinecount(false);
                                }
                            }
                        }
                        // 检查是否获胜
                        if (checkGameWin()) {
                            winGame();
                        }

                    }

                });
            }
        }
        setMines();
    }

    //游戏获胜
    private void winGame() {
        stopTimer();
        isTimerStarted = false;
        isGameOver = true;
        Mines = 0;
        //将所有雷块置为不可点击，标记所有的地雷
        for (int row = 0; row < numberOfRowsInMineField; row++) {
            for (int column = 0; column < numberOfColumnsInMineField; column++) {
                blocks[row][column].setEnabled(false);
                if (blocks[row][column].hasMine()) {
                    blocks[row][column].setFlagIcon(true);
                }
            }
        }
        GameRecord(1);//进行记录
        //弹出获胜对话框
        new AlertDialog.Builder(this).setTitle("Win")
                .setMessage("你赢了!\n时间：" + time)
                .setNegativeButton("确定", null)
                .setPositiveButton("新游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        init();
                    }
                })
                .create().show();
    }

    //游戏失败
    private void finishGame() {
        stopTimer();
        isGameOver = true;
        isTimerStarted = false;
        //将所有雷块置为不可点击，翻开所有雷块
        for (int row = 0; row < numberOfRowsInMineField; row++) {
            for (int column = 0; column < numberOfColumnsInMineField; column++) {
                blocks[row][column].setEnabled(false);
                if (blocks[row][column].hasMine() && blocks[row][column].isFlagged()) continue;
                else {
                    if (blocks[row][column].hasMine()) {
                        blocks[row][column].setMineIcon();
                    } else {
                        blocks[row][column].OpenBlock();
                    }
                }
            }
        }
        GameRecord(0);//进行记录
        //弹出失败对话框
        new AlertDialog.Builder(this).setTitle("Lose")
                .setMessage("你输了!\n时间：" + time)
                .setNegativeButton("确定", null)
                .setPositiveButton("新游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        init();
                    }
                })
                .create().show();
    }

    //游戏结果存档
    public void GameRecord(int t) {
        String state;
        String filename = getExternalCacheDir().getAbsolutePath() + "/gamerecord.txt";//记录文件的路径
        FileOutputStream fos;
        FileInputStream fis;
        PrintWriter pw = null;
        BufferedReader br = null;
        if (t == 1) state = "Win  ";
        else state = "Lose";
        //若目录路径不存在，建立目录
        File file = new File(getExternalCacheDir().getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            File dir = new File(filename);
            if (!dir.exists()) dir.createNewFile();//若记录文件不存在创建文件
                //若记录文件存在，检查记录是否为5条，若达到5条则删去最早记录
            else {
                LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
                lnr.skip(Long.MAX_VALUE);
                int i = lnr.getLineNumber();//获取行数，即记录数
                lnr.close();
                //将除最早记录之外的其他记录存入list中再写回
                if (i >= 5) {
                    fis = new FileInputStream(filename);
                    br = new BufferedReader(new InputStreamReader(fis));
                    i = 0;
                    String str = null;
                    ArrayList<String> list = new ArrayList<String>();
                    while ((str = br.readLine()) != null) {
                        if (i == 0) {
                            i++; continue;
                        }
                        i++;
                        list.add(str);
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                    for (String a : list) {
                        bw.write(a);
                        bw.newLine();
                    }
                    bw.close();
                    fis.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //写入新记录
        try {
            fos = new FileOutputStream(filename, true);
            pw = new PrintWriter(fos);
            pw.println(Level + "       " + state + "       " + time);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    //更新地雷计数器
    public void UpdateMinecount(boolean flag) {
        if (flag) {
            Flagged--;
            Mines++;
        } else {
            Flagged++;
            Mines--;
        }
        if (Mines < 0) Mines = 0;
        if (Mines < 10) {
            txtMineCount.setText("0" + Integer.toString(Mines));
        } else {
            txtMineCount.setText(Integer.toString(Mines));
        }
    }

    //启动计时器
    public void startTimer() {
        if (Seconds == 0) {
            timer.removeCallbacks(updateTimeElasped);
            // 让计时器每隔1s更新界面
            timer.postDelayed(updateTimeElasped, 1000);
        } else {
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer() {
        // 停止计时器
        timer.removeCallbacks(updateTimeElasped);
    }

    //计时器子线程
    private Runnable updateTimeElasped = new Runnable() {
        public void run() {
            ++Seconds;
            String hh = new DecimalFormat("00").format(Seconds / 3600);
            String mm = new DecimalFormat("00").format(Seconds % 3600 / 60);
            String ss = new DecimalFormat("00").format(Seconds % 60);
            time = hh + ":" + mm + ":" + ss;
            txtTimer.setText(time);
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };

    //显示雷区
    private void showMineField() {
        for (int i = 0; i < numberOfRowsInMineField; i++) {
            for (int j = 0; j < numberOfColumnsInMineField; j++) {
                GridLayout.Spec rowSpec = GridLayout.spec(i); // 设置btn的行
                GridLayout.Spec columnSpec = GridLayout.spec(j);// 设置btn的列
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                Resources resources = this.getResources();//获取系统资源
                DisplayMetrics dm = resources.getDisplayMetrics();//获取屏幕密度
                int t = dm.widthPixels / (numberOfColumnsInMineField + 1);//根据屏幕宽度设置雷块宽度
                params.width = t;
                params.height = t;
                //根据雷块数量调整宽度和间距
                if (Level.equals("Hard")) {
                    params.setMargins(3, 3, 3, 3);
                    params.height += 20;
                } else if (Level.equals("Easy")) {
                    params.setMargins(5, 5, 5, 5);
                }
                MineField.addView(blocks[i][j], params);//单元格加入网格布局
            }
        }
    }

    //布雷并计算每个方块周围雷数
    private void setMines() {
        Random random = new Random();
        for (int i = 0; i < totalNumberOfMines; i++) {
            while (true) {
                Block randomBlock = blocks[random.nextInt(numberOfRowsInMineField)][random.nextInt(numberOfColumnsInMineField)];
                if (!randomBlock.hasMine()) {
                    randomBlock.plantMine();
                    break;
                }
            }
        }
        //计算每个无雷方块周围雷数
        for (int i = 0; i < numberOfRowsInMineField; i++) {
            for (int j = 0; j < numberOfColumnsInMineField; j++) {
                if (blocks[i][j].hasMine())
                    continue;
                int mineCount = 0;
                mineCount = getMineCount(j, i);
                blocks[i][j].setNumberOfMinesInSurrounding(mineCount);
            }
        }
    }

    private int getMineCount(int x, int y) {
        int mineCount = 0;
        for (int i = (y - 1); i <= (y + 1); i++) {
            for (int j = (x - 1); j <= (x + 1); j++) {
                if (i == y && j == x)
                    continue;
                try {
                    if (blocks[i][j].hasMine())
                        mineCount++;
                } catch (IndexOutOfBoundsException ex) {
                    continue;
                }

            }
        }
        return mineCount;
    }

    //翻开自己及周围方块
    private void rippleUncover(int rowClicked, int columnClicked) {
        // 若方块有雷或被标记则不翻开
        if (blocks[rowClicked][columnClicked].hasMine() || blocks[rowClicked][columnClicked].isFlagged())
            return;

        //翻开方块
        blocks[rowClicked][columnClicked].OpenBlock();
        Opened++;
        //若待翻开的方块周围有雷则不再翻开其周围
        if (blocks[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0) {
            return;
        }
        // 翻开周围八个方块
        for (int i = (rowClicked - 1); i <= (rowClicked + 1); i++) {
            for (int j = (columnClicked - 1); j <= (columnClicked + 1); j++) {
                try {
                    if (i == rowClicked && j == columnClicked) continue;
                    if (!blocks[i][j].isCovered()) continue;
                    rippleUncover(i, j);//递归处理周围方块
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        }
        return;
    }

    //判断游戏是否胜利
    private boolean checkGameWin() {
        if (isGameOver) return false;
        //标记数和翻开数之和等于方块总数，并且标记数等于雷数则游戏胜利
        if (Flagged + Opened == numberOfColumnsInMineField * numberOfRowsInMineField
                && totalNumberOfMines == Flagged) {
            return true;
        }
        return false;
    }
}