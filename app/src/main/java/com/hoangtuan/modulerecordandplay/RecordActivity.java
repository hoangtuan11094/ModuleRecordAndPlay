package com.hoangtuan.modulerecordandplay;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {
    byte[] bData = null;

    AudioRecord recorder;
    private Thread recordingThread = null;
    private Thread recordingThreadCount = null;

    private String filename = "";
    private boolean isRecording = false;
    private static final int RECORDER_SAMPLERATE = 44100;

    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private Handler mHandler = new Handler();
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    int i=0;
    TextView txtDem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        final SimpleDateFormat df = new SimpleDateFormat("mm:ss");
txtDem=(TextView)findViewById(R.id.txtDem);

        recorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
    RecordActivity.this.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            i+=1000;
            txtDem.setText(df.format(i));
        mHandler.postDelayed(this,1000);
        }
    });
        recordingThread = new Thread(new Runnable() {

            public void run() {

                writeAudioDataToFile();

            }
        }, "AudioRecorder Thread");
        recordingThread.start();

    }
    private void writeAudioDataToFile() {
        // Write the output audio in byte
        File file_folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HVT_V1/");
        if (!file_folder.exists())
        {
            file_folder.mkdirs();
            File nomediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HVT_V1/.nomedia");
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(filename.equals(""))
        {
            SimpleDateFormat df = new SimpleDateFormat("hh-mm-ss-SSS aa - dd MMM yyy", Locale.ENGLISH);
            Date today = Calendar.getInstance().getTime();
            filename = df.format(today);
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/HVT_V1/"+filename+".pcm";

        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // writes the data to file from buffer stores the voice buffer
                bData = short2byte(sData);

                os.write(bData, 0, BufferElements2Rec * BytesPerElement);

            } catch (IOException e) {
                e.printStackTrace();
            }}

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    public void onStop(View view) {

            // stops the recording activity
            if (null != recorder) {
                isRecording = false;


                recorder.stop();
                recorder.release();

                recorder = null;
                recordingThread = null;

                Intent intent=new Intent(RecordActivity.this,ReviewActivity.class);
                intent.putExtra("file_name",filename);
                startActivity(intent);
                finish();

        }
    }




}
