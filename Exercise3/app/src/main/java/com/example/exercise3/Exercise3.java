package com.example.exercise3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise3);

        buttonUp = findViewById(R.id.buttonUp);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonExit = findViewById(R.id.buttonExit);
        listView = findViewById(R.id.listView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            try {
                listDirectories(Environment.getExternalStorageDirectory().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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
                if (musicFiles != null && musicFiles.size() > 0) {
                    Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
                    intent.putExtra("listMusic", (ArrayList) musicFiles);
                    startActivity(intent);
                } else {
                    Toast.makeText(Exercise3.this, "No music files in the current directory", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileSystemModel selectedEntry = directoryEntries.get(position);
                String path = selectedEntry.getPath();
                File selectedFile = new File(path);
                if (selectedFile.isDirectory()) {
                    try {
                        listDirectories(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(Exercise3.this, "Open file: " + selectedFile.getName(), Toast.LENGTH_SHORT).show();
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
                    if (bitmap != null)
                    item.setAlbumName(bitmap);

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

    private boolean isMusicFile(String fileName) {
        String[] supportedExtensions = {".mp3", ".wav", ".ogg", ".mp4"};
        for (String extension : supportedExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
