package com.tikjuti.bai2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateNote extends AppCompatActivity {

    ImageButton btnUndo;
    ImageButton btnSaveNote;
    ImageButton btnInsertImage;
    EditText addNoteContent;
    EditText addNoteTitle;
    Database database;
    ImageView noteImage;
    private static final int REQ_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);

        database = MainNote.database;
        btnUndo = findViewById(R.id.addUndoButton);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        addNoteTitle = findViewById(R.id.addNoteTitle);
        addNoteContent = findViewById(R.id.addNoteContent);
        btnInsertImage = findViewById(R.id.btnInsertImage);
        noteImage = findViewById(R.id.noteImage);

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainNote.class);
                startActivity(intent);
            }
        });

        btnInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, REQ_PICK_IMAGE);
            }
        });
        noteImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(CreateNote.this);
                dialog.setMessage("Bạn chắc muốn xóa?");
                dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(new byte[1024],0 ,new byte[1024].length);
                        noteImage.setImageBitmap(bitmap);
                    }
                });
                dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
                return true;
            }
        });
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = addNoteTitle.getText().toString();
                String content = addNoteContent.getText().toString();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.ENGLISH);
                String dateTime = formatter.format(calendar.getTime());

                byte[] imagePath = new byte[1024];

                Drawable drawable = noteImage.getDrawable();
                if (drawable != null && drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap imageBitmap = bitmapDrawable.getBitmap();
                    if (imageBitmap != null) {
                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                        imagePath = byteArray.toByteArray();
                    }
                }
                if (title.equals("") || content.equals("")) {
                    Toast.makeText(CreateNote.this, "Vui lòng nhập đầy đủ các mục", Toast.LENGTH_SHORT).show();
                    return;
                }
                database.insertData(title, content, imagePath, dateTime);
                Toast.makeText(CreateNote.this, "Đã thêm", Toast.LENGTH_LONG).show();
                MainNote.getTitleData();
                Intent intent = new Intent(getApplicationContext(), MainNote.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    noteImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
