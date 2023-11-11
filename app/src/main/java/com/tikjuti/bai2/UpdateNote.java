package com.tikjuti.bai2;

import static android.R.*;
import static android.R.string.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

public class UpdateNote extends AppCompatActivity {

    ImageButton btnUndo;
    EditText editNoteTitle;
    EditText editNoteContent;
    ImageButton btnEditSaveNote;
    ImageButton btnEditInsertImage;
    ImageButton btnEditRemind;
    ImageView noteEditImage;
    Database database;
    private AlertDialog promptDateTimeDialog;
    private DatePicker pkrDate;
    private TimePicker pkrTime;
    private Locale curLocale;
    private static final int REQ_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_note);

        database = MainNote.database;
        btnUndo = findViewById(R.id.editUndoButton);
        editNoteTitle = findViewById(R.id.editNoteTitle);
        editNoteContent = findViewById(R.id.editNoteContent);
        btnEditSaveNote = findViewById(R.id.btnEditSaveNote);
        btnEditInsertImage = findViewById(R.id.btnEditInsertImage);
        btnEditRemind = findViewById(R.id.btnEditRemind);
        noteEditImage = findViewById(R.id.noteEditImage);

        Bundle bundle = getIntent().getExtras();

        int id = bundle.getInt("id");
        String title = bundle.getString("title");
        String content = bundle.getString("content");
        byte[] imagePath = bundle.getByteArray("imagePath");
        String dateTime = bundle.getString("dateTime");

        editNoteTitle.setText(title);
        editNoteContent.setText(content);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagePath,0 ,imagePath.length);
        noteEditImage.setImageBitmap(bitmap);

        btnEditInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, REQ_PICK_IMAGE);
            }
        });

        btnEditRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (promptDateTimeDialog == null) {
                    view = getLayoutInflater().inflate(R.layout.dialog_time_picker, null, false);
                    pkrDate = view.findViewById(R.id.pkrDate);
                    pkrTime = view.findViewById(R.id.pkrTime);
                    pkrTime.setIs24HourView(true);
                    promptDateTimeDialog = new AlertDialog.Builder(view.getContext())
                            .setTitle("Set reminder time")
                            .setView(view)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Xử lý khi người dùng chọn "OK"
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();

                }
                Calendar instance = Calendar.getInstance(curLocale);
                pkrDate.updateDate(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
                int hour = instance.get(Calendar.HOUR_OF_DAY), minute = instance.get(Calendar.MINUTE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pkrTime.setHour(hour);
                    pkrTime.setMinute(minute);
                } else {
                    pkrTime.setCurrentHour(hour);
                    pkrTime.setCurrentMinute(minute);
                }
                promptDateTimeDialog.show();
            }
        });

        btnEditSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = editNoteTitle.getText().toString();
                String newContent = editNoteContent.getText().toString();
                if (newTitle.equals("") || newContent.equals("")) {
                    Toast.makeText(UpdateNote.this, "Vui lòng nhập đầy đủ các mục", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] imagePath = new byte[1024];

                Drawable drawable = noteEditImage.getDrawable();
                if (drawable != null && drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap imageBitmap = bitmapDrawable.getBitmap();
                    if (imageBitmap != null) {
                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                        imagePath = byteArray.toByteArray();
                    }
                }

                database.updateData(id, newTitle, newContent, imagePath, dateTime);
                Toast.makeText(UpdateNote.this, "Đã cập nhật", Toast.LENGTH_LONG).show();
                MainNote.getTitleData();
                Intent intent = new Intent(getApplicationContext(), MainNote.class);
                startActivity(intent);
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    noteEditImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
