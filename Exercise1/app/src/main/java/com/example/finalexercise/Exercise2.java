package com.example.finalexercise;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Exercise2 extends AppCompatActivity {

    private TextView textView;
    private Button buttonUp, buttonSelect, buttonExit;
    private ListView listView;
    private List<String> fileList;
    private String currentFolderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise2);

        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        buttonUp = findViewById(R.id.buttonUp);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonExit = findViewById(R.id.buttonExit);
        String PhoneModel = android.os.Build.MODEL;
        Log.d("model",PhoneModel);
        fileList = new ArrayList<>();
        currentFolderPath = Environment.getExternalStorageDirectory().getPath();
        displayFilesAndFolders(currentFolderPath);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            }
        });
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUp();
            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAndPlayMusic();
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void displayFilesAndFolders(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            fileList.clear();
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.d("FileList", "Directory: " + file.getName());
                    fileList.add(file.getName());
                } else {
                    Log.d("FileListFile", "File: " + file.getName());
                    fileList.add(file.getName());
                }
            }
        }

        // Update the TextView with the current directory path
        textView.setText("Current Directory: " + folderPath);

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
