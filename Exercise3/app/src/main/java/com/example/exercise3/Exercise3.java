package com.example.exercise3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class Exercise3 extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 123;

    ArrayList<AudioModel> songList = new ArrayList<>();
    private TextView textView;
    private Button buttonUp, buttonSelect, buttonExit;
    private RecyclerView recyclerView;
    private List<String> fileList;
    private String currentFolderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise3);

        // Check and request permission if needed
        if (checkStoragePermission()) {
            initializeApp();
        } else {
            requestStoragePermission();
        }
    }

    private void initializeApp() {
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.textView);
        buttonUp = findViewById(R.id.buttonUp);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonExit = findViewById(R.id.buttonExit);
        fileList = new ArrayList<>();
        currentFolderPath = Environment.getExternalStorageDirectory().getPath();
        displayFilesAndFolders(currentFolderPath);

        String[] protection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, protection, selection, null, null);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedFileName = fileList.get(i);
            String selectedFilePath = currentFolderPath + File.separator + selectedFileName;
            File selectedFile = new File(selectedFilePath);
            if (selectedFile.isDirectory()) {
                // If the selected item is a directory, update the current folder path
                currentFolderPath = selectedFilePath;
                displayFilesAndFolders(currentFolderPath);
            } else {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = i;
                Intent intent = new Intent(this, MusicPlayer.class);
                intent.putExtra("LIST", songList);
                startActivity(intent);
            }
        });

        buttonUp.setOnClickListener(view -> goUp());

        buttonSelect.setOnClickListener(view -> selectAndPlayMusic());

        buttonExit.setOnClickListener(view -> finish());
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the app
                initializeApp();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission Denied. Exiting the app.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void displayFilesAndFolders(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            fileList.clear();
            for (File file : files) {
                Log.d(file.isDirectory() ? "FileList" : "FileListFile", (file.isDirectory() ? "Directory: " : "File: ") + file.getName());
                fileList.add(file.getName());
            }
        }

        // Update the TextView with the current directory path
        textView.setText(folderPath);

        // Use ArrayAdapter to bind the data to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);
    }

    private void goUp() {
        File currentFolder = new File(currentFolderPath);
        File parentFolder = currentFolder.getParentFile();

        if (parentFolder != null) {
            currentFolderPath = parentFolder.getPath();
            displayFilesAndFolders(currentFolderPath);

        }

        // Check if the current folder is the root and disable the buttonUp accordingly
    }

    private boolean isMp3File(String fileName) {
        return fileName.toLowerCase().endsWith(".mp3");
    }

    private void selectAndPlayMusic() {
        // TODO: Implement logic to play selected music
    }
}
