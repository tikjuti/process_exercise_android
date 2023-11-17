package com.example.exercise3;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {

    TextView title, times,timesTotal;
    SeekBar seekBar;
    ImageView next, pausePlay, previous, music_img;
    ArrayList<File> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer;
    int position = 0;
    Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);

        title = findViewById(R.id.title);
        times = findViewById(R.id.times);
        seekBar = findViewById(R.id.seek_bar);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        pausePlay = findViewById(R.id.pausePlay);
        music_img = findViewById(R.id.music_img);
        timesTotal = findViewById(R.id.timesTotal);
        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);

        Intent intent = getIntent();
        if (intent.hasExtra("listMusic")) {
            songList = (ArrayList<File>) intent.getSerializableExtra("listMusic");
        }

        initPlayer();
        setTimesTotal();
        updateTimeSong();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > songList.size()-1){
                    position = 0;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                initPlayer();
                mediaPlayer.start();
                pausePlay.setImageResource(R.drawable.baseline_pause_24);
                setTimesTotal();
                updateTimeSong();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position < 0){
                    position = songList.size()-1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                initPlayer();
                mediaPlayer.start();
                pausePlay.setImageResource(R.drawable.baseline_pause_24);
                setTimesTotal();
                updateTimeSong();
            }
        });

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    music_img.clearAnimation();
                    pausePlay.setImageResource(R.drawable.baseline_play_arrow_24);
                } else {
                    mediaPlayer.start();
                    music_img.startAnimation(animation);
                    pausePlay.setImageResource(R.drawable.baseline_pause_24);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }
    private void initPlayer() {
        mediaPlayer = MediaPlayer.create(MusicPlayer.this, Uri.parse(songList.get(position).getAbsolutePath()));
        mediaPlayer.start();
        title.setText(songList.get(position).getName());
        music_img.startAnimation(animation);
    }
    private void setTimesTotal() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        timesTotal.setText(format.format(mediaPlayer.getDuration())+"");
        seekBar.setMax(mediaPlayer.getDuration());
    }
    private void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                times.setText(format.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        position++;
                        if (position > songList.size()-1){
                            position = 0;
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        initPlayer();
                        mediaPlayer.start();
                        pausePlay.setImageResource(R.drawable.baseline_pause_24);
                        setTimesTotal();
                        updateTimeSong();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

}
