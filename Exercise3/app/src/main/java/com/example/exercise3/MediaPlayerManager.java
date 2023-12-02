package com.example.exercise3;

import android.media.MediaPlayer;

import java.io.File;
import java.util.List;

public class MediaPlayerManager {
    private List<File> musicFiles;
    private MediaPlayer mediaPlayer;



    public List<File> getMusicFiles() {
        return musicFiles;
    }

    public void setMusicFiles(List<File> musicFiles) {
        this.musicFiles = musicFiles;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}

