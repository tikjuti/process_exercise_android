package com.example.exercise4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int NOTIFICATION_REMINDER = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PackageManager.PERMISSION_GRANTED);
        createNotificationChannel();
        scheduleDailyNotification();
    }

    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 8 PM
        calendar.set(Calendar.MINUTE, 32);
        calendar.set(Calendar.SECOND, 0);

        // If the current time is already past 8:55 PM, set it for the next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent notifyIntent = new Intent(MainActivity.this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_REMINDER, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, // Repeat every day
                pendingIntent
        );
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RemindPhotoChannel";
            String description = "Channel for remind taking photo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyPhoto", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cameraButton) {
            // Check if the CAMERA permission is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, open the camera
                openCamera();
            } else {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            return;
        }
    }

    // Add onActivityResult method to handle the result if needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the captured image, if needed
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImageToGallery(imageBitmap);
        }
    }

    private void saveImageToGallery(Bitmap imageBitmap) {
        // Lấy đường dẫn để lưu ảnh vào thư mục Pictures trên bộ nhớ ngoại vi
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Tạo định dạng thời gian để đặt tên file theo định dạng "yyyyMMdd_HHmmss"
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        // Đặt tên file kết hợp với timeStamp để có tên file duy nhất
        String fileName = "IMG_" + timeStamp + ".jpg";
        // Tạo đối tượng File với đường dẫn và tên file đã xác định
        File imageFile = new File(storageDir, fileName);

        try {
            // Mở một OutputStream để ghi dữ liệu vào file
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            // Nén và ghi dữ liệu từ Bitmap vào file dưới định dạng JPEG với chất lượng 100
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            // Đảm bảo rằng tất cả dữ liệu đã được ghi và đóng OutputStream
            outputStream.flush();
            outputStream.close();
            // Tạo Intent để cập nhật thư viện ảnh với file mới thêm vào
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            // Gửi Intent để hệ thống biết về file mới
            sendBroadcast(mediaScanIntent);
            // Hiển thị thông báo ngắn cho người dùng về việc lưu ảnh thành công
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Xử lý các ngoại lệ, không có hành động cụ thể ở đây
        }
    }

}