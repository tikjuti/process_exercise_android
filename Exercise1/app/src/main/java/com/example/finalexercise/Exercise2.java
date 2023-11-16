package com.example.finalexercise;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Exercise2 extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 123;

    private TextView textView;
    private Button buttonUp, buttonSelect, buttonExit;
    private ListView listView;
    private List<String> fileList;
    private String currentFolderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise2);

        // Check and request permission if needed
        if (checkStoragePermission()) {
            initializeApp();
        } else {
            requestStoragePermission();
        }
    }

    private void initializeApp() {
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        buttonUp = findViewById(R.id.buttonUp);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonExit = findViewById(R.id.buttonExit);
        fileList = new ArrayList<>();
        currentFolderPath = Environment.getExternalStorageDirectory().getPath();
        displayFilesAndFolders(currentFolderPath);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedFileName = fileList.get(i);
            String selectedFilePath = currentFolderPath + File.separator + selectedFileName;
            File selectedFile = new File(selectedFilePath);
            if (selectedFile.isDirectory()) {
                // If the selected item is a directory, update the current folder path
                currentFolderPath = selectedFilePath;
                displayFilesAndFolders(currentFolderPath);
            } else {
                // Handle file click, for example, play the selected music
                // Add your logic here
            }
        });

        buttonUp.setOnClickListener(view -> goUp());

        buttonSelect.setOnClickListener(view -> selectAndPlayMusic());

        buttonExit.setOnClickListener(view -> finish());
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
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
    }

    private void selectAndPlayMusic() {
        // TODO: Implement logic to play selected music
    }
}
