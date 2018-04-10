package com.hoangtuan.modulerecordandplay;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    Button btnXemLichSu;
    private RecyclerView recyclerView;
    AdapterWave adapterWave;
    ArrayList<WaveModel> waveModels;
    String filename;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        btnXemLichSu = (Button) findViewById(R.id.btnXemLichSu);
        btnXemLichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReviewActivity.this, HistoryActivity.class));
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recy);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        waveModels = new ArrayList<>();

        waveModels.add(new WaveModel("Bình Thường", 50000));
        waveModels.add(new WaveModel("Người Già", 38000));
        waveModels.add(new WaveModel("Trẻ Con", 90000));
        waveModels.add(new WaveModel("Sóc", 100000));


        Intent intent = getIntent();

        if (intent != null) {
            if (intent.getStringExtra("file_name") != null) {
                filename = intent.getStringExtra("file_name");
            }
        }
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_V1/" + filename + ".pcm";
        adapterWave = new AdapterWave(this, waveModels, filePath);
        recyclerView.setAdapter(adapterWave);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ReviewActivity.this, MainActivity.class));
    }
}
