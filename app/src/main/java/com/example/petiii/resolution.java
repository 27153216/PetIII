package com.example.petiii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Time;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class resolution extends AppCompatActivity {

    static final String db_name="petDB"; //整個資料庫名稱
    SQLiteDatabase db;
    String s;
    Timer timer = new Timer(true);
    long endtime, remaining;
    double showtime;
    int earn = 0;

    TextView txvtime, txvresult;
    Button btnend;
    ImageView img, img2;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolution);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態列


        Intent it = getIntent();
        s = it.getStringExtra("id");

        txvtime = findViewById(R.id.txvtime);
        txvresult = findViewById(R.id.txvresult);
        btnend = findViewById(R.id.btnend);
        img = findViewById(R.id.imgcat);
        img2 = findViewById(R.id.imgfox);
        Glide.with(this).load(R.drawable.movingr).into(img);
        Glide.with(this).load(R.drawable.fox_walk).into(img2);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Date dt = new Date();
                remaining = (endtime - dt.getTime()) / 100;
                showtime = 1.0 * remaining / 10;
                if (showtime <=0){
                    txvtime.setText("挑戰結束");
                    ending();
                }else{
                    txvtime.setText("剩餘時間：" + showtime + "秒");
                }

            }
        };


        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        endtime = cur.getLong(cur.getColumnIndex("endtime"));
        int staging = cur.getInt(cur.getColumnIndex("stage"));
        String[] stage = getResources().getStringArray(R.array.stage);
        txvresult.setText( stage[staging - 1]+ " 挑戰中...");

        timer.schedule(new MyTimerTask(), 100, 100);
    }

    public void ending(){
        timer.cancel();
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        int stagen = cur.getInt(cur.getColumnIndex("stage"));
        int res = cur.getInt(cur.getColumnIndex("result"));
        if (res == 1){ //成功
            cur = db.rawQuery("select * from stage where stagen = '" + stagen + "'", null);
            cur.moveToFirst();
            earn = cur.getInt(cur.getColumnIndex("earn"));
            txvresult.setText("挑戰成功，獲得" + earn + "金！");
        }else{ //失敗
            txvresult.setText("挑戰失敗，再接再厲");
            earn = 0;
        }

        btnend.setVisibility(View.VISIBLE);
    }

    public void clickend(View v){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        long money = cur.getLong(cur.getColumnIndex("money"));
        money += earn;
        ContentValues cv = new ContentValues();
        cv.put("money", money);
        cv.put("battling", 0);
        db.update("ac", cv, "id='"+ s + "'", null);

        Intent it = new Intent(this, ready.class);
        it.putExtra("id", s);
        startActivity(it);
    }

    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
            handler.sendEmptyMessage(0);
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
