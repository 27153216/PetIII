package com.example.petiii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class battleSelect extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView txvneedlv, txvearn, txvtext;
    Spinner spinner;
    static final String db_name="petDB"; //整個資料庫名稱
    SQLiteDatabase db;
    String s;
    int stagetime, rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_select);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態列

        txvneedlv = findViewById(R.id.txvstory);
        txvearn = findViewById(R.id.txvearn);
        txvtext = findViewById(R.id.txvtext);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        Intent it = getIntent();
        s = it.getStringExtra("id");

        refresh();
    }

    public void refresh(){
        int selectstage = spinner.getSelectedItemPosition() + 1;
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        int playerlv = cur.getInt(cur.getColumnIndex("catlv")) + cur.getInt(cur.getColumnIndex("foxlv"));
        cur = db.rawQuery("select * from stage where stagen = '" + selectstage + "'", null);
        cur.moveToFirst();
        stagetime = cur.getInt(cur.getColumnIndex("time"));
        int needlv = cur.getInt(cur.getColumnIndex("needlv"));
        rate = playerlv * 100 / needlv ;
        if (rate > 100) rate = 100;
        txvneedlv.setText("建議等級：" + needlv + "／目前等級：" + playerlv + "／成功率：" + rate + "％");
        txvearn.setText("獎勵：" + cur.getInt(cur.getColumnIndex("earn")) + "金／所需時間：" + cur.getInt(cur.getColumnIndex("time")) + "秒");
        txvtext.setText("關卡介紹：\n" + cur.getString(cur.getColumnIndex("text")));

    }

    public void back(View v){
        finish();
    }

    public void start(View v){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues(1);
        cv.put("battling", 1);
        db.update("ac", cv, "id='"+ s + "'", null);

        Date dt = new Date();
        cv = new ContentValues();
        cv.put("starttime", dt.getTime());
        cv.put("endtime", dt.getTime() + stagetime * 1000);
        cv.put("stage", spinner.getSelectedItemPosition() +1);
        cv.put("rate", rate);
        db.update("ac", cv, "id='"+ s + "'", null);

        Random r = new Random();
        int res = r.nextInt(101);
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        int rate = cur.getInt(cur.getColumnIndex("rate"));
        int stagen = cur.getInt(cur.getColumnIndex("stage"));
        if (res < rate){ //成功
            cv = new ContentValues();
            cv.put("result", 1);
            db.update("ac", cv, "id='"+ s + "'", null);
        }else{ //失敗
            cv = new ContentValues();
            cv.put("result", 0);
            db.update("ac", cv, "id='"+ s + "'", null);
        }

        Intent it = new Intent(this, resolution.class);
        it.putExtra("id", s);
        startActivity(it);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        refresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
