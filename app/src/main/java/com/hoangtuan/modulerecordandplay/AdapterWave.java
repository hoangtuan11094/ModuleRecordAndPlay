package com.hoangtuan.modulerecordandplay;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by atbic on 10/4/2018.
 */

public class AdapterWave extends RecyclerView.Adapter<AdapterWave.WaveHolder> implements SeekBar.OnSeekBarChangeListener {
    Context context;
    ArrayList<WaveModel> waveModels;
    String filepath;
    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    private boolean seekMoving = false;
    private static final int RECORDER_SAMPLERATE = 44100;
    String filename="";


    public AdapterWave(Context context, ArrayList<WaveModel> waveModels, String filepath) {
        this.context = context;
        this.waveModels = waveModels;
        this.filepath = filepath;
        SimpleDateFormat df = new SimpleDateFormat("hh-mm-ss-SSS aa - dd MMM yyy", Locale.ENGLISH);
        Date today = Calendar.getInstance().getTime();
        filename = df.format(today);

    }

    public AdapterWave() {
    }

    @Override
    public AdapterWave.WaveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wave, parent, false);
        return new WaveHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterWave.WaveHolder holder, final int position) {
         WaveModel waveModel = waveModels.get(position);
        holder.txt.setText(waveModel.getName());

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wave(waveModels.get(position).getSampling());
                playWave(holder.skBar, holder.txt);
            }
        });

        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wave(waveModels.get(position).getSampling());
                copyFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_V1/", "Wave" + ".wav", filename+waveModels.get(position).getName() + ".wav", Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_SAVE/");
                Toast.makeText(context, "Đã lưu tại " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_SAVE/" + waveModels.get(position).getName() + ".wav", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyFile(String inputPath, String inputFile, String outputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return waveModels.size();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mediaPlayer != null && fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekMoving = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekMoving = false;
    }

    public class WaveHolder extends RecyclerView.ViewHolder {
        TextView txt;
        Button btnPlay, btnSave;
        SeekBar skBar;

        public WaveHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.txt);
            btnPlay = (Button) itemView.findViewById(R.id.btnPlay);
            btnSave = (Button) itemView.findViewById(R.id.btnSave);
            skBar = (SeekBar) itemView.findViewById(R.id.skBar);


        }
    }

    private void Wave(int i) {
        try {
            File f1 = new File(filepath);
            File f2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_V1/" + "Wave" + ".wav");
            rawToWave(f1, f2, i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playWave(final SeekBar seekBar, final TextView textView) {
        textView.setVisibility(View.GONE);
        seekBar.setVisibility(View.VISIBLE);
        if (mediaPlayer == null) {
            File audio = null;
            audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_V1/" + "Wave" + ".wav");

            Uri myUri = Uri.fromFile(audio); // initialize Uri here
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mediaPlayer.setDataSource(context, myUri);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    seekBar.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            });
            seekBar.setMax(mediaPlayer.getDuration());
            ((ReviewActivity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mediaPlayer != null && !seekMoving && mediaPlayer.isPlaying()) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 50);
                }
            });

            mediaPlayer.start();
        } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }
    }

    private void rawToWave(final File rawFile, final File waveFile, final int waveSampling) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, waveSampling); // sample rate
            writeInt(output, RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }
            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
        // Adding echo
        //Clone original Bytes
        byte[] bytesTemp = fullyReadFileToBytes(rawFile);
        byte[] temp = bytesTemp.clone();
        RandomAccessFile randomAccessFile = new RandomAccessFile(waveFile, "rw");
        //seek to skip 44 bytes
        randomAccessFile.seek(44);
        //Echo
        int N = RECORDER_SAMPLERATE / 8;
        for (int n = N + 1; n < bytesTemp.length; n++) {
            bytesTemp[n] = (byte) (temp[n] + .3 * temp[n - N]);
        }
        randomAccessFile.write(bytesTemp);
        randomAccessFile.close();


    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }
}
