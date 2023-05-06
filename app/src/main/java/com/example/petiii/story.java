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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class story extends AppCompatActivity implements View.OnClickListener {

    TextView txv;
    String s;
    static final String db_name="petDB"; //整個資料庫名稱
    SQLiteDatabase db;
    int i = 1, textcount;
    ImageView imv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態列

        txv = findViewById(R.id.txvstory);
        txv.setOnClickListener(this);

        imv = findViewById(R.id.charaimg);

        Intent it = getIntent();
        s = it.getStringExtra("id");

        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from story", null);
        textcount = cur.getCount();

        showtext(1);
    }


    @Override
    public void onClick(View view) {
        i++;
        if (i <= textcount){
            showtext(i);
        }else{
            endofstory();
            Intent it = new Intent(this, ready.class);
            it.putExtra("id", s);
            startActivity(it);
        }
    }

    public void showtext(int page){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from story where rowid = " + page, null);
        cur.moveToFirst();
        String showname = cur.getString(cur.getColumnIndex("charaname"));
        if (showname.compareTo("playerName") == 0){
            Cursor cur2 = db.rawQuery("select * from ac where id = '" + s + "'", null);
            cur2.moveToFirst();
            showname = cur2.getString(cur2.getColumnIndex("name"));
        }
        txv.setText(showname + "\n" + cur.getString(cur.getColumnIndex("text")));
        int chara = 0;
        switch (cur.getString(cur.getColumnIndex("chara"))){
            case "cat_angry":
                chara = R.drawable.cat_angry;
                break;
            case "cat_happy":
                chara = R.drawable.cat_happy;
                break;
            case "cat_normal":
                chara = R.drawable.cat_normal;
                break;
            case "cat_sad":
                chara = R.drawable.cat_sad;
                break;
            case "fox_angry":
                chara = R.drawable.fox_angry;
                break;
            case "fox_happy":
                chara = R.drawable.fox_happy;
                break;
            case "fox_normal":
                chara = R.drawable.fox_normal;
                break;
            case "fox_sad":
                chara = R.drawable.fox_sad;
                break;
            case "noPicture":
                chara = android.R.color.transparent;
                break;
        }
        imv.setImageResource(chara);
    }

    //看完劇情執行這個
    public void endofstory(){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();
        cv.put("story", 1);
        db.update("ac", cv, "id='"+ s + "'", null);
    }

    public void skip(View v){
        endofstory();
        Intent it = new Intent(this, ready.class);
        it.putExtra("id", s);
        startActivity(it);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
