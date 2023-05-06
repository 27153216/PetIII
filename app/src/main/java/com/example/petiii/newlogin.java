package com.example.petiii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class newlogin extends AppCompatActivity {

    static final String db_name="petDB"; //整個資料庫名稱
    static final String tb_name="ac"; //帳號資料表名稱
    SQLiteDatabase db;
    String s = ""; //卡號
    EditText username;
    TextView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlogin);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態列

        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        username = findViewById(R.id.username);
        card = findViewById(R.id.card);

        Intent it = getIntent();
        s = it.getStringExtra("id");

        card.setText("卡號：" + s);
    }


    private void addData(String id, String name){

        ContentValues cv = new ContentValues(2);
        cv.put("id", id);
        cv.put("name", name);
        cv.put("catlv", 1);
        cv.put("foxlv", 1);
        cv.put("money", 0);
        cv.put("story", 0);
        cv.put("battling", 0);

        db.insert(tb_name, null, cv);

    }

    public void sign(View v){
        if (username.getText().toString().isEmpty()){
            Toast.makeText(this,"請輸入角色名稱",Toast.LENGTH_LONG).show();
            return;
        }
        String name = username.getText().toString();
        addData(s, name);
        Toast.makeText(this,"註冊成功",Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void cancel(View v){
        startActivity(new Intent(this, MainActivity.class));
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
