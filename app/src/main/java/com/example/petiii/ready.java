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
import android.widget.TextView;
import android.widget.Toast;

public class ready extends AppCompatActivity {

    String s;
    TextView txvcatlv,txvfoxlv, txvcatneed, txvfoxneed, usermoney;
    static final String db_name="petDB"; //整個資料庫名稱
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態列

        txvcatlv = findViewById(R.id.txvcatlv);
        txvfoxlv = findViewById(R.id.txvfoxlv);
        txvcatneed = findViewById(R.id.txvcatneed);
        txvfoxneed = findViewById(R.id.txvfoxneed);
        usermoney = findViewById(R.id.usermoney);

        Intent it = getIntent();
        s = it.getStringExtra("id");

        refresh();
    }

    public void upcat(View v){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);

        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        int catlv = cur.getInt(cur.getColumnIndex("catlv"));
        int money = cur.getInt(cur.getColumnIndex("money"));



        if (catlv >= 10){
            Toast.makeText(this,"等級已滿！",Toast.LENGTH_LONG).show();
            return;
        }
        cur = db.rawQuery("select needmoney from petlevel where lv = '" + catlv + "'", null);
        cur.moveToFirst();
        int need = cur.getInt(cur.getColumnIndex("needmoney"));
        if(money < need){
            Toast.makeText(this,"所持金不足！",Toast.LENGTH_LONG).show();
        }else{
            money -= need;
            catlv++;
            ContentValues cv = new ContentValues(2);
            cv.put("catlv", catlv);
            cv.put("money", money);
            db.update("ac", cv, "id='"+ s + "'", null);
        }
        refresh();
    }



    public void upfox(View v){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);

        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        int foxlv = cur.getInt(cur.getColumnIndex("foxlv"));
        int money = cur.getInt(cur.getColumnIndex("money"));



        if (foxlv >= 10){
            Toast.makeText(this,"等級已滿！",Toast.LENGTH_LONG).show();
            return;
        }
        cur = db.rawQuery("select needmoney from petlevel where lv = '" + foxlv + "'", null);
        cur.moveToFirst();
        int need = cur.getInt(cur.getColumnIndex("needmoney"));
        if(money < need){
            Toast.makeText(this,"所持金不足！",Toast.LENGTH_LONG).show();
        }else{
            money -= need;
            foxlv++;
            ContentValues cv = new ContentValues(2);
            cv.put("foxlv", foxlv);
            cv.put("money", money);
            db.update("ac", cv, "id='"+ s + "'", null);
        }
        refresh();
    }

    public void givemoney(View v){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues(1);
        cv.put("money", 10000);
        db.update("ac", cv, "id='"+ s + "'", null);
        refresh();
    }

    public void refresh() {
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        int catlv = cur.getInt(cur.getColumnIndex("catlv"));
        int foxlv = cur.getInt(cur.getColumnIndex("foxlv"));
        txvcatlv.setText("LV."+ catlv);
        txvfoxlv.setText("LV."+ foxlv);
        if (catlv < 10){
            cur = db.rawQuery("select * from petlevel where lv = '"+ catlv +"'", null);
            cur.moveToFirst();
            txvcatneed.setText("需求："+cur.getInt(cur.getColumnIndex("needmoney")));
        }else{
            txvcatneed.setText("等級已滿！");
        }
        if (foxlv < 10){
            cur = db.rawQuery("select * from petlevel where lv = '"+ foxlv +"'", null);
            cur.moveToFirst();
            txvfoxneed.setText("需求："+cur.getInt(cur.getColumnIndex("needmoney")));
        }else{
            txvfoxneed.setText("等級已滿！");
        }
        cur = db.rawQuery("select * from ac where id = '"+ s +"'", null);
        cur.moveToFirst();
        usermoney.setText("所持金："+cur.getInt(cur.getColumnIndex("money")));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public void start(View v){
        Intent it = new Intent(this, battleSelect.class);
        it.putExtra("id", s);
        startActivity(it);
    }
}
