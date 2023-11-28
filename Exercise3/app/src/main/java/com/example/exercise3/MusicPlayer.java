package com.example.exercise3;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicPlayer extends AppCompatActivity {
    TextView title, times, timesTotal;
    SeekBar seekBar;
    ImageView next, pausePlay, previous, back, repeat, randomPlay;
    CircleImageView music_img;
    ArrayList<File> songList = new ArrayList<>();
    static MediaPlayer mediaPlayer = new MediaPlayer();
    int position = 0;
    private ObjectAnimator anim;
    private boolean isRepeat = false;
    private boolean isRandom = false;
    Random random;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);

        mediaPlayer = Exercise3.mediaPlayer;

        title = findViewById(R.id.title);
        times = findViewById(R.id.times);
        seekBar = findViewById(R.id.seek_bar);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        pausePlay = findViewById(R.id.pausePlay);
        music_img = findViewById(R.id.music_img);
        timesTotal = findViewById(R.id.timesTotal);
        back = findViewById(R.id.back);
        repeat = findViewById(R.id.repeat);
        randomPlay = findViewById(R.id.randomPlay);
        anim = ObjectAnimator.ofFloat(music_img, "rotation", 0, 360);
        anim.setInterpolator(null);
        anim.setDuration(30000);
        anim.setRepeatCount(ValueAnimator.INFINITE);


        Intent intent = getIntent();
        if (intent.hasExtra("listMusicSliding")) {
            songList = (ArrayList<File>) intent.getSerializableExtra("listMusicSliding");
            position = (int) intent.getSerializableExtra("position");
            initPlayer();
            setTimesTotal();
            updateTimeSong();
        }

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRepeat = !isRepeat;
                mediaPlayer.setLooping(isRepeat);
                repeat.setImageResource(isRepeat ? R.drawable.baseline_repeat_active_24 : R.drawable.baseline_repeat_24);
                pausePlay.setImageResource(R.drawable.baseline_pause_24);
                setTimesTotal();
                updateTimeSong();
            }
        });
        randomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRandom = !isRandom;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            randomNextSong();
                        }
                    });
                } else {
                    randomNextSong();
                }
                randomPlay.setImageResource(isRandom ? R.drawable.baseline_shuffle_active_24 : R.drawable.baseline_shuffle_24);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    int currentPosition = position;
                    Intent intentBack = new Intent(getApplicationContext(), Exercise3.class);
                    intentBack.putExtra("currentSong", currentPosition);
                    intentBack.putExtra("songList", songList);
                    startActivity(intentBack);
                } else {
                    Intent intentBack = new Intent(getApplicationContext(), Exercise3.class);
                    startActivity(intentBack);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > songList.size() - 1) {
                    position = 0;
                }
                initPlayer();
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(MusicPlayer.this, Uri.parse(songList.get(position).getAbsolutePath()));
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
                if (position < 0) {
                    position = songList.size() - 1;
                }
                initPlayer();
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(MusicPlayer.this, Uri.parse(songList.get(position).getAbsolutePath()));
                mediaPlayer.start();
                pausePlay.setImageResource(R.drawable.baseline_pause_24);
                setTimesTotal();
                updateTimeSong();
            }
        });

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    anim.pause();
                    pausePlay.setImageResource(R.drawable.baseline_play_arrow_24);
                } else {
                    mediaPlayer.start();
                    anim.resume();
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
        title.setText(customTitle(songList.get(position).getName()));
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(songList.get(position).getAbsolutePath());
        Bitmap bitmap = retriever.getFrameAtTime(6000000);
        byte[] picture = retriever.getEmbeddedPicture();
        if (picture != null) {
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            music_img.setImageBitmap(bitmapPicture);
        } else if (bitmap != null)
            music_img.setImageBitmap(bitmap);
        else
            music_img.setImageResource(R.drawable.placeholder);
        if (mediaPlayer.isPlaying()) {
            anim.start();
            pausePlay.setImageResource(R.drawable.baseline_pause_24);
        }
        else {
            anim.resume();
            pausePlay.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }
    private void setTimesTotal() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        timesTotal.setText(format.format(mediaPlayer.getDuration()) + "");
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
                if (!isRandom) {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            position++;
                            if (position > songList.size() - 1) {
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
                }
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    private String customTitle(String fileName) {
        String[] supportedExtensions = {".mp3", ".wav", ".ogg", ".mp4", ".aac", ".ogg", ".m4a", ".wma", ".aiff"};
        for (String extension : supportedExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                fileName = fileName.substring(0, fileName.length() - 4);
                return fileName;
            }
        }
        return null;
    }

    private void randomNextSong() {
        random = new Random();
        int randomIndex = random.nextInt(songList.size());
        position = randomIndex;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songList.get(position).getAbsolutePath()));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isRandom) {
            title.setText(customTitle(songList.get(position).getName()));
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(songList.get(position).getAbsolutePath());
            Bitmap bitmap = retriever.getFrameAtTime(6000000);
            byte[] picture = retriever.getEmbeddedPicture();
            if (picture != null) {
                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                music_img.setImageBitmap(bitmapPicture);
            } else if (bitmap != null)
                music_img.setImageBitmap(bitmap);
            else
                music_img.setImageResource(R.drawable.placeholder);
            pausePlay.setImageResource(R.drawable.baseline_pause_24);
            setTimesTotal();
            updateTimeSong();
        }
    }
}
