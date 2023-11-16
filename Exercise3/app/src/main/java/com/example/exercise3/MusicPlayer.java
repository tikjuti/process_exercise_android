package com.example.exercise3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {

    TextView title, times;
    SeekBar seekBar;
    ImageView next, pausePlay, previous, music_img;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;

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

        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResWithMusic();
    }

    void setResWithMusic() {
        currentSong = songList.get(MyMediaPlayer.currentIndex);
        title.setText(currentSong.getTitle());
        times.setText(convertToMMSS(currentSong.getDuration()));
        playMusic();
    }

    private void playMusic() {

    }

    private void playNextSong() {

    }

    private void playPreviousSong() {

    }

    private void pausePlay() {

    }

    public static String convertToMMSS(String duration) {
        Long mills = Long.parseLong(duration);
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mills) % TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(mills) % TimeUnit.HOURS.toSeconds(1));
    }
}
