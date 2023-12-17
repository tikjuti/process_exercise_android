package com.tikjuti.bai2;

import static android.R.*;
import static android.R.string.*;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.List;
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
    private static final int REQ_REMINDER = 3;

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
     /*   String title = bundle.getString("title");
        String content = bundle.getString("content");
        byte[] imagePath = bundle.getByteArray("imagePath");
        String dateTime = bundle.getString("dateTime");*/

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
                                    handlePositiveButtonClick();
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

        String finalDateTime = dateTime;
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

                database.updateData(id, newTitle, newContent, imagePath, finalDateTime);
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
        noteEditImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateNote.this);
                dialog.setMessage("Bạn chắc muốn xóa?");
                dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(new byte[1024],0 ,new byte[1024].length);
                        noteEditImage.setImageBitmap(bitmap);
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

    private void handlePositiveButtonClick() {
        Calendar instance = Calendar.getInstance(curLocale);
        Calendar set = (Calendar) instance.clone();
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = pkrTime.getHour();
            minute = pkrTime.getMinute();
        } else {
            hour = pkrTime.getCurrentHour();
            minute = pkrTime.getCurrentMinute();
        }
        set.set(pkrDate.getYear(), pkrDate.getMonth(), pkrDate.getDayOfMonth(), hour, minute, 5);
        if (set.compareTo(instance) < 0) {
            Toast.makeText(UpdateNote.this, "Bạn không được phép đặt thời gian nhắc nhở trong quá khứ!", Toast.LENGTH_SHORT).show();
            return;
        }
        long millis = set.getTimeInMillis();
        NoteAdapter adapter = new NoteAdapter();
        
        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        ContentValues values = new ContentValues();
        values.put("Reminder", millis);

        SQLiteDatabase db = database.getReadableDatabase();
        db.update("Notes", values, "Id = ?", new String[]{String.valueOf(id)});
        adapter.notifyDataSetChanged();
        Context context = UpdateNote.this;
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        Uri parse = Uri.parse("nre-time:" + millis);
        intent.setData(parse);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, REQ_REMINDER, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_ONE_SHOT);
        ((AlarmManager) context.getSystemService(ALARM_SERVICE))
                .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() - instance.getTimeInMillis() + millis,
                        broadcast);
        Toast.makeText(this, "Đã đặt hẹn giờ thông báo!", Toast.LENGTH_SHORT).show();
    }
}
