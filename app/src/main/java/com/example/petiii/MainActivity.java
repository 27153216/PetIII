package com.example.petiii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    static final String db_name="petDB"; //整個資料庫名稱
    static final String tb_name="ac"; //帳號資料表名稱
    SQLiteDatabase db;
    String s = ""; //卡號
    TextView txv;
    Button btnlogin,btnsignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態列


        txv = findViewById(R.id.txvstory);
        btnlogin = findViewById(R.id.btnlogin);
        btnsignin = findViewById(R.id.btnsignin);

        //以下資料庫初始化
        importSQL();
        //以上資料庫初始化

        //以下NFC相關
        NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();

        if (adapter == null){
            Toast.makeText(this,"沒有NFC功能",Toast.LENGTH_LONG).show();
        }else if(!adapter.isEnabled()){
            Toast.makeText(this,"請開啟NFC功能",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS)); //前往設定NFC頁面
        }
        //以上NFC相關

        Stetho.initializeWithDefaults(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent it = getIntent();

        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(it.getAction())) {
            Tag tag = it.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null){
                txv.setText("讀取錯誤");
            }else{
                byte[] tagID = tag.getId();
                for (int i = 0; i < tagID.length; i++){
                    s += Integer.toHexString(tagID[i] & 0xFF);
                }
//                txv.setText(s+"已登入");
                Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
                if (cur.getCount() == 0){
                    txv.setText("偵測到新的卡片，是否要新增玩家?");
                    btnsignin.setVisibility(View.VISIBLE);
                }else{
                    cur.moveToFirst();
                    txv.setText("偵測到現有玩家「" + cur.getString(cur.getColumnIndex("name")) + "」，是否登入?");
                    btnlogin.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void test1(View v){
        s = "123";
        singin(v);
    }

    public void test2(View v){
        s = "123";
        login(v);
    }

    public void singin(View v){
        Intent it = new Intent(this, newlogin.class);
        it.putExtra("id", s);
        startActivity(it);
    }

    public void login(View v){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("select * from ac where id = '" + s + "'", null);
        cur.moveToFirst();
        Intent it;
        if (cur.getInt(cur.getColumnIndex("story")) == 0){
            it = new Intent(this, story.class);
        }else if(cur.getInt(cur.getColumnIndex("battling")) == 0){
            it = new Intent(this, ready.class);
        }else {
            it = new Intent(this, resolution.class);
        }
        it.putExtra("id", s);
        startActivity(it);
    }


    private void importSQL(){
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        String createTable="create table if not exists " + tb_name + " (id varchar(50), name varchar(50), catlv int, foxlv int, money long,story int,battling int,starttime long, endtime long,stage int,rate int,result int)";
        db.execSQL(createTable);

        //以下是寵物升級需求
        createTable = "create table if not exists petlevel (lv int, needmoney int)";
        db.execSQL(createTable);
        Cursor cur = db.rawQuery("select * from petlevel", null);
        if (cur.getCount() == 0){
            ContentValues cv = new ContentValues(2);
            cv.put("lv", 1); cv.put("needmoney", 10);
            db.insert("petlevel", null, cv);
            cv.put("lv", 2); cv.put("needmoney", 50);
            db.insert("petlevel", null, cv);
            cv.put("lv", 3); cv.put("needmoney", 100);
            db.insert("petlevel", null, cv);
            cv.put("lv", 4); cv.put("needmoney", 200);
            db.insert("petlevel", null, cv);
            cv.put("lv", 5); cv.put("needmoney", 500);
            db.insert("petlevel", null, cv);
            cv.put("lv", 6); cv.put("needmoney", 1000);
            db.insert("petlevel", null, cv);
            cv.put("lv", 7); cv.put("needmoney", 3000);
            db.insert("petlevel", null, cv);
            cv.put("lv", 8); cv.put("needmoney", 5000);
            db.insert("petlevel", null, cv);
            cv.put("lv", 9); cv.put("needmoney", 10000);
            db.insert("petlevel", null, cv);
        }
        //以上是寵物升級需求

        createTable = "create table if not exists story (chara varchar(255), charaname varchar(255), text varchar(255))";
        db.execSQL(createTable);
        cur = db.rawQuery("select * from story", null);
        if (cur.getCount() == 0){
            ContentValues cv = new ContentValues();
            String[] alltext = getResources().getStringArray(R.array.storytext);
            for (int i = 0; i < alltext.length; i++){
                String[] text = alltext[i].split("#");
                cv.put("chara", text[0]); cv.put("charaname", text[1]); cv.put("text", text[2]);
                db.insert("story", null, cv);
            }
        }

        createTable = "create table if not exists stage (stagen int, needlv int, text varchar(255), earn int, time int)";
        db.execSQL(createTable);
        cur = db.rawQuery("select * from stage", null);
        if (cur.getCount() == 0){
            ContentValues cv = new ContentValues(5);
            cv.put("stagen", 1); cv.put("needlv", 2); cv.put("text", "同學會用計算機嗎？"); cv.put("earn", 10); cv.put("time", 3);
            db.insert("stage", null, cv); cv.put("time", 3);
            cv.put("stagen", 2); cv.put("needlv", 5); cv.put("text", "嘿，啊如果你練習完了就可以回去了，嘿"); cv.put("earn", 50); cv.put("time", 3);
            db.insert("stage", null, cv);
            cv.put("stagen", 3); cv.put("needlv", 8); cv.put("text", "Viagra怎麼念，上課都沒在聽啊\nViagra念五遍"); cv.put("earn", 100); cv.put("time", 5);
            db.insert("stage", null, cv);
            cv.put("stagen", 4); cv.put("needlv", 10); cv.put("text", "這個指令就是這樣打"); cv.put("earn", 200); cv.put("time", 10);
            db.insert("stage", null, cv);
            cv.put("stagen", 5); cv.put("needlv", 12); cv.put("text", "如果你真的對程式沒有興趣，可以考慮轉學或轉系"); cv.put("earn", 500); cv.put("time", 20);
            db.insert("stage", null, cv);
            cv.put("stagen", 6); cv.put("needlv", 15); cv.put("text", "要睡覺的話回家睡效益比較高\n要打工就全力打工一個月六萬。"); cv.put("earn", 1000); cv.put("time", 30);
            db.insert("stage", null, cv);
            cv.put("stagen", 7); cv.put("needlv", 18); cv.put("text", "同學，現在不是考試時間，不用緊張"); cv.put("earn", 3000); cv.put("time", 40);
            db.insert("stage", null, cv);
            cv.put("stagen", 8); cv.put("needlv", 20); cv.put("text", "柏0同學很棒，終於發生問題，這就是我想看到的。"); cv.put("earn", 5000); cv.put("time", 50);
            db.insert("stage", null, cv);
            cv.put("stagen", 9); cv.put("needlv", 100); cv.put("text", "來手機手機，手機交到前面。\n這不是我要的！沒有搔到癢處！\n你們都沒有看老師的影片，幹嘛來上課？\n你們都很有錢，學費都爸媽繳\n有沒有人知道答案？不要講話！"); cv.put("earn", 10000); cv.put("time", 60);
            db.insert("stage", null, cv);
            cv.put("stagen", 10); cv.put("needlv", 2); cv.put("text", "我們最後30分鐘時間來上python"); cv.put("earn", 5000); cv.put("time", 3);
            db.insert("stage", null, cv);
            cv.put("stagen", 11); cv.put("needlv", 2); cv.put("text", "哇好厲害，同學都很厲害，你們都很厲害啊。"); cv.put("earn", 10000); cv.put("time", 5);
            db.insert("stage", null, cv);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

}
