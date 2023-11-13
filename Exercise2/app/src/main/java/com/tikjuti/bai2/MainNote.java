package com.tikjuti.bai2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class MainNote extends AppCompatActivity {

    ImageButton btnAddNote;
    ImageButton moreInfoButton;
    ListView listNote;
    static Database database;

    static ArrayList<Note> arrayNote;
    static NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_note);

        database = new Database(this);

        btnAddNote = findViewById(R.id.addNoteButton);
        moreInfoButton = findViewById(R.id.btnMoreInfo);

        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainNote.this, "More information", Toast.LENGTH_SHORT).show();
            }
        });
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateNote.class);
                startActivity(intent);
            }
        });

        listNote = (ListView) findViewById(R.id.listViewNote);
        arrayNote = new ArrayList<>();

        adapter = new NoteAdapter(this, R.layout.line_note, arrayNote);
        listNote.setAdapter(adapter);
        getTitleData();
    }

    public static void getTitleData() {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor dataTitle = db.query("Notes", null, null, null, null, null, null);
        arrayNote.clear();
        while (dataTitle.moveToNext()) {
            String title = dataTitle.getString(1);
            String content = dataTitle.getString(2);
            String dateTime = dataTitle.getString(4);
            int id = dataTitle.getInt(0);
            Note newNote = new Note();
            newNote.setId(id);
            newNote.setTitle(title);
            newNote.setContent(content);
            newNote.setDateTime(dateTime);
            arrayNote.add(newNote);
        }
        adapter.notifyDataSetChanged();

    }

    public void dialogDelete(int id) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Bạn chắc muốn xóa?");
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLiteDatabase db = database.getWritableDatabase();
                long newRowId = db.delete("Notes", "Id = ?", new String[]{String.valueOf(id)});
                if (newRowId != -1) {
                    Toast.makeText(MainNote.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainNote.this, "Xóa bị lỗi", Toast.LENGTH_SHORT).show();
                }
                db.close();
                getTitleData();
            }
        });
        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }
    public void updateNote(int id) {

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor dataById = db.query("Notes", null, "Id = ?", new String[]{String.valueOf(id)}, null, null, null);
        String title = "",content ="", dateTime="";
        byte[] imagePath = new byte[1024];

        while (dataById.moveToNext()) {
            title = dataById.getString(1);
            content = dataById.getString(2);
            imagePath = dataById.getBlob(3);
            dateTime = dataById.getString(4);
        }
        Intent iGetContactInfo = new Intent(getApplicationContext(), UpdateNote.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putByteArray("imagePath", imagePath);
        bundle.putString("dateTime", dateTime);
        iGetContactInfo.putExtras(bundle);

        startActivity(iGetContactInfo);
    }

    }