package com.hoangtuan.modulerecordandplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity {
RecyclerView recyHis;
HistoryAdapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyHis=(RecyclerView) findViewById(R.id.recyHis);

        LinearLayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyHis.setHasFixedSize(true);
        recyHis.setLayoutManager(manager);

        historyAdapter=new HistoryAdapter(this);
        recyHis.setAdapter(historyAdapter);
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
//    }
}
