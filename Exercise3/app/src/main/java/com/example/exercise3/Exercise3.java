package com.example.exercise3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Exercise3 extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 123;
    private int currentSongIndex = 0;
    private TextView textView;
    private Button buttonUp, buttonSelect, buttonExit;
    private ListView listView;
    private List<String> directoryEntries = new ArrayList<>();
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
            listDirectories(Environment.getExternalStorageDirectory().toString());
        }

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDirectory.equals(Environment.getExternalStorageDirectory().toString())) {
                    File currentDirFile = new File(currentDirectory);
                    String parentDir = currentDirFile.getParent();
                    listDirectories(parentDir);
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
                String selectedEntry = directoryEntries.get(position);
                String path = currentDirectory + File.separator + selectedEntry;
                File selectedFile = new File(path);

                if (selectedFile.isDirectory()) {
                    listDirectories(path);
                } else {
                    Toast.makeText(Exercise3.this, "Open file: " + selectedFile.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void listDirectories(String directoryPath) {
        currentDirectory = directoryPath;
        setTitle("" + currentDirectory);

        directoryEntries.clear();
        musicFiles = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                directoryEntries.add(file.getName());
            }
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, directoryEntries);
        listView.setAdapter(directoryList);
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
