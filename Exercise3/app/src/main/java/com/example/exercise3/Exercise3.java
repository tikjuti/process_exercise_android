package com.example.exercise3;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exercise3 extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 123;
    private Button buttonUp, buttonSelect, buttonExit;
    private ListView listView;
    private List<FileSystemModel> directoryEntries = new ArrayList<>();
    private String currentDirectory;
    private List<File> musicFiles;
    private List<File> musicFilesTmp;
    static MediaPlayer mediaPlayer;
    LinearLayout controls;
    static ObjectAnimator anim;
    int position = 0;
    ImageView imageMain, previousMain, playMain, nextMain;
    TextView titleMain;
    static MediaPlayerManager mediaPlayerManager;
    Intent intent;
    private boolean hasInitializedNext = false;
    private boolean hasInitializedPrevious = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);

        buttonUp = findViewById(R.id.buttonUp);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonExit = findViewById(R.id.buttonExit);
        listView = findViewById(R.id.listView1);
        controls = findViewById(R.id.controls);
        titleMain = findViewById(R.id.titleMain);
        imageMain = findViewById(R.id.imageMain);
        previousMain = findViewById(R.id.previousMain);
        playMain = findViewById(R.id.playMain);
        nextMain = findViewById(R.id.nextMain);

        anim = ObjectAnimator.ofFloat(imageMain, "rotation", 0, 360);
        anim.setInterpolator(null);
        anim.setDuration(30000);
        anim.setRepeatCount(ValueAnimator.INFINITE);

        mediaPlayerManager = new MediaPlayerManager();
        mediaPlayer = mediaPlayerManager.getMediaPlayer();

        intent = getIntent();
        if (intent.hasExtra("currentSong")) {
            mediaPlayerManager = MusicPlayer.mediaPlayerManager;
            mediaPlayer = mediaPlayerManager.getMediaPlayer();
            musicFiles = mediaPlayerManager.getMusicFiles();
            mediaPlayerManager.setMusicFiles(musicFiles);
            position = (int) intent.getSerializableExtra("currentSong");
            if (mediaPlayer.isPlaying()) {
            titleMain.setText(customTitle(musicFiles.get(position).getName()));
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(musicFiles.get(position).getAbsolutePath());
            Bitmap bitmap = retriever.getFrameAtTime(6000000);
            byte[] picture = retriever.getEmbeddedPicture();
            if (picture != null) {
                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                imageMain.setImageBitmap(bitmapPicture);
            } else if (bitmap != null)
                imageMain.setImageBitmap(bitmap);
            else
                imageMain.setImageResource(R.drawable.placeholder);
            anim.start();
            playMain.setImageResource(R.drawable.baseline_pause_24);}
            else {
                unInitPlayer();
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            try {
                listDirectories(Environment.getExternalStorageDirectory().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        controls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim.pause();
                Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        playMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.hasExtra("currentSong")) {
                    mediaPlayerManager = MusicPlayer.mediaPlayerManager;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    anim.pause();
                    playMain.setImageResource(R.drawable.baseline_play_arrow_24);
                } else {
                    mediaPlayer.start();
                    anim.start();
                    playMain.setImageResource(R.drawable.baseline_pause_24);
                }
            }
        });
        nextMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInitializedNext && intent.hasExtra("currentSong")) {
                    mediaPlayerManager = MusicPlayer.mediaPlayerManager;
                    mediaPlayer = mediaPlayerManager.getMediaPlayer();
                    musicFiles = mediaPlayerManager.getMusicFiles();
                    position = (int) intent.getSerializableExtra("currentSong");
                    hasInitializedNext = true;
                }

                position++;
                if (position > musicFiles.size() - 1) {
                    position = 0;
                }

                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                 initPlayer();
            }
        });
        previousMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasInitializedPrevious && intent.hasExtra("currentSong")) {
                    mediaPlayerManager = MusicPlayer.mediaPlayerManager;
                    mediaPlayer = mediaPlayerManager.getMediaPlayer();
                    musicFiles = mediaPlayerManager.getMusicFiles();
                    position = (int) intent.getSerializableExtra("currentSong");
                    hasInitializedPrevious = true;
                }
                position--;
                if (position < 0) {
                    position = musicFiles.size() - 1;
                }
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                initPlayer();
            }
        });
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDirectory.equals(Environment.getExternalStorageDirectory().toString())) {
                    File currentDirFile = new File(currentDirectory);
                    String parentDir = currentDirFile.getParent();
                    try {
                        listDirectories(parentDir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listMusicFile(currentDirectory);
                musicFilesTmp = musicFiles;
                mediaPlayerManager.setMusicFiles(musicFilesTmp);
                if (musicFiles != null && musicFiles.size() > 0) {
                    initPlayer();
                } else {
                    Toast.makeText(Exercise3.this, "No music files in the current directory", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positions, long id) {
                FileSystemModel selectedEntry = directoryEntries.get(positions);
                String path = selectedEntry.getPath();
                File selectedFile = new File(path);
                if (selectedFile.isDirectory()) {
                    try {
                        listDirectories(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (isMusicFile(selectedFile.getName())) {
                        position = positions;
                        mediaPlayer.stop();
                        mediaPlayer = MediaPlayer.create(Exercise3.this, Uri.parse(selectedFile.getAbsolutePath()));
                        mediaPlayer.start();
                        titleMain.setText(customTitle(selectedFile.getName()));
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(selectedFile.getAbsolutePath());
                        Bitmap bitmap = retriever.getFrameAtTime(6000000);
                        byte[] picture = retriever.getEmbeddedPicture();
                        if (picture != null) {
                            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                            imageMain.setImageBitmap(bitmapPicture);
                        } else if (bitmap != null)
                            imageMain.setImageBitmap(bitmap);
                        else
                            imageMain.setImageResource(R.drawable.placeholder);
                        anim.start();
                        playMain.setImageResource(R.drawable.baseline_pause_24);
                        updateNextSong();
                    } else {
                        Toast.makeText(Exercise3.this, "This is not file music", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @SuppressLint("SuspiciousIndentation")
    private void listDirectories(String directoryPath) throws IOException {
        currentDirectory = directoryPath;
        setTitle("" + currentDirectory);

        directoryEntries.clear();
        musicFiles = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                FileSystemModel item = new FileSystemModel();
                item.setTitle(file.getName());
                item.setPath(file.getAbsolutePath());
                item.setFolder(file.isDirectory());

                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(file.getAbsolutePath());
                    Bitmap bitmap = retriever.getFrameAtTime(10000000);
                    byte[] picture = retriever.getEmbeddedPicture();
                    if (picture != null) {
                        Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                        item.setAlbumImage(bitmapPicture);
                    } else {
                        if (bitmap != null)
                            item.setAlbumImage(bitmap);
                        else {
                            int drawableId = R.drawable.placeholder;
                            Bitmap bitmapR = BitmapFactory.decodeResource(this.getResources(), drawableId);
                            item.setAlbumImage(bitmapR);
                        }
                    }
                    String albumName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    if (albumName != null)
                        item.setAlbumName(albumName);
                    retriever.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                directoryEntries.add(item);
            }
        }
        ListAdapter customAdapter = new ListAdapter(this, directoryEntries);
        listView.setAdapter(customAdapter);
    }

    private void listMusicFile(String directoryPath) {
        currentDirectory = directoryPath;
        directoryEntries.clear();
        musicFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (isMusicFile(file.getName())) {
                    musicFiles.add(file);
                }
            }
        }
    }
    private void initPlayer() {
        mediaPlayer = MediaPlayer.create(Exercise3.this, Uri.parse(musicFiles.get(position).getAbsolutePath()));
        mediaPlayer.start();
        mediaPlayerManager.setMediaPlayer(mediaPlayer);
        titleMain.setText(customTitle(musicFiles.get(position).getName()));
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(musicFiles.get(position).getAbsolutePath());
        Bitmap bitmap = retriever.getFrameAtTime(6000000);
        byte[] picture = retriever.getEmbeddedPicture();
        if (picture != null) {
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            imageMain.setImageBitmap(bitmapPicture);
        } else if (bitmap != null)
            imageMain.setImageBitmap(bitmap);
        else
            imageMain.setImageResource(R.drawable.placeholder);
        anim.start();
        playMain.setImageResource(R.drawable.baseline_pause_24);
        updateNextSong();
    }
    private void unInitPlayer() {
        titleMain.setText(customTitle(musicFiles.get(position).getName()));
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(musicFiles.get(position).getAbsolutePath());
        Bitmap bitmap = retriever.getFrameAtTime(6000000);
        byte[] picture = retriever.getEmbeddedPicture();
        if (picture != null) {
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            imageMain.setImageBitmap(bitmapPicture);
        } else if (bitmap != null)
            imageMain.setImageBitmap(bitmap);
        else
            imageMain.setImageResource(R.drawable.placeholder);
        anim.pause();
        playMain.setImageResource(R.drawable.baseline_play_arrow_24);
    }
    private void updateNextSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        position++;
                        if (position > musicFiles.size() - 1) {
                            position = 0;
                        }
                        initPlayer();
                        playMain.setImageResource(R.drawable.baseline_pause_24);
                        updateNextSong();
                    }
                });
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
    private boolean isMusicFile(String fileName) {
        String[] supportedExtensions = {".mp3", ".wav", ".ogg", ".mp4", ".aac", ".ogg", ".m4a", ".wma", ".aiff"};
        for (String extension : supportedExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
