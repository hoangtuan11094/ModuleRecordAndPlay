package com.hoangtuan.modulerecordandplay;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by atbic on 10/4/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    Context context;
    private ArrayList<String> filenames;
    private MediaPlayer mediaPlayer;

    public HistoryAdapter(Context context) {
        this.context = context;
        int itemNo = 0;
        filenames = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_SAVE";
        File f = new File(path);
        if (f != null) {
            File file[] = f.listFiles();
            if (file != null) {

                for (int i = 0; i < file.length; i++) {
                    if (!file[i].getName().contains(".nomedia")) {
                        String a = file[i].getName().substring(0, file[i].getName().lastIndexOf('.'));
                        filenames.add(a);
                    }
                }
            }
        }

    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, final int position) {
        holder.txtHis.setText(filenames.get(position));
    }

    public void onRemove(int position) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_SAVE/" + filenames.get(position) + ".wav");
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
            }
        }
        filenames.remove(position);
        notifyItemRemoved(position);
    }

    public void onPlay(int position) {
        if (mediaPlayer == null) {
            File audio = null;
            audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVT_SAVE/" + filenames.get(position) + ".wav");

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

                }
            });


            mediaPlayer.start();
        } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }

    }

    @Override
    public int getItemCount() {
        return filenames.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        TextView txtHis;
        Button btnPlayH;
        Button btnDeleteH;

        public HistoryHolder(View itemView) {
            super(itemView);
            txtHis = (TextView) itemView.findViewById(R.id.txtHis);
            btnDeleteH = (Button) itemView.findViewById(R.id.btnDeleteH);
            btnPlayH = (Button) itemView.findViewById(R.id.btnPlayH);
            btnPlayH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlay(getAdapterPosition());
                }
            });
            btnDeleteH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRemove(getAdapterPosition());
                }
            });
        }
    }
}
