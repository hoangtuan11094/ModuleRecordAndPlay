package com.hoangtuan.modulerecordandplay;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file_folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HVT_SAVE/");
        if (!file_folder.exists())
        {
            file_folder.mkdirs();
            File nomediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HVT_SAVE/.nomedia");
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRecord(View view) {
        Intent intent=new Intent(MainActivity.this,RecordActivity.class);
        startActivity(intent);
        finish();
    }

    public void onHistory(View view) {
        startActivity(new Intent(MainActivity.this,HistoryActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
