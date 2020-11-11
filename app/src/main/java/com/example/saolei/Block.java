package com.example.saolei;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class Block extends androidx.appcompat.widget.AppCompatButton {

    private boolean isCovered; // 块是否覆盖
    private boolean isMined; // 块是否是地雷
    private boolean isFlagged; // 是否将该块标记为一个地雷
    private int numberOfMinesInSurrounding; // 在附近的地雷数量块

    public Block(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public Block(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public Block(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //设置默认参数
    public void setDefaults() {
        isCovered = true;//是否未翻开
        isMined = false;//是否为雷
        isFlagged = false;//是否被标记
        numberOfMinesInSurrounding = 0;//周围雷数
        this.setBackgroundResource(R.drawable.unselected);//设置未翻开背景图
        setBoldFont();
    }

    //设置翻开状态
    public void setNumberOfSurroundingMines(int number) {
        this.setBackgroundResource(R.drawable.selected);//设置翻开背景图
        updateNumber(number);//设置周围雷数
    }

    //添加雷块标识
    public void setMineIcon() {
        this.setBackgroundResource(R.drawable.dl);
    }

    //添加标记标识
    public void setFlagIcon(boolean enabled) {

        if (!enabled) {
            this.setBackgroundResource(R.drawable.hq);
        } else {
            this.setTextColor(Color.BLACK);
        }
    }

    //清除所有标记
    public void clearAllIcons() {
        this.setText("");
        this.setTextColor(R.drawable.unselected);
    }

    private void setBoldFont() {
        this.setTypeface(null, Typeface.BOLD);
    }

    //翻开方块
    public void OpenBlock() {
        if (!isCovered) {
            return;
        }
        isCovered = false;
        //如果为雷设置地雷标识
        if (hasMine()) {
            setMineIcon();
        } else {
            setNumberOfSurroundingMines(numberOfMinesInSurrounding);//根据周围雷数设置翻开状态及显示数字
        }
    }

    //显示周围雷数
    public void updateNumber(int text) {
        if (text != 0) {
            this.setText(Integer.toString(text));
            switch (text) {
                case 1:
                    this.setTextColor(Color.BLUE);
                    break;
                case 2:
                    this.setTextColor(Color.rgb(0, 100, 0));
                    break;
                case 3:
                    this.setTextColor(Color.RED);
                    break;
                case 4:
                    this.setTextColor(Color.rgb(85, 26, 139));
                    break;
                case 5:
                    this.setTextColor(Color.rgb(139, 28, 98));
                    break;
                case 6:
                    this.setTextColor(Color.rgb(238, 173, 14));
                    break;
                case 7:
                    this.setTextColor(Color.rgb(47, 79, 79));
                    break;
                case 8:
                    this.setTextColor(Color.rgb(71, 71, 71));
                    break;
                case 9:
                    this.setTextColor(Color.rgb(205, 205, 0));
                    break;
            }
        }

    }

    //布雷
    public void plantMine() {
        isMined = true;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public boolean hasMine() {
        return isMined;
    }

    public void setNumberOfMinesInSurrounding(int number) {
        numberOfMinesInSurrounding = number;
    }

    public int getNumberOfMinesInSorrounding() {
        return numberOfMinesInSurrounding;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }
}
