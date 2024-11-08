package com.example.aethoneventsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView imageViewQRCode;
    private Button share;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        imageViewQRCode = findViewById(R.id.imageViewQRCode);

        // Retrieve the eventId passed from OrganizerActivity
        int eventId = getIntent().getIntExtra("eventId", -1);

        // Generate and display the QR code
        generateQRCode(String.valueOf(eventId));

        share = findViewById(R.id.share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android Q and above, save the image using MediaStore
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Screenshots");
                values.put(MediaStore.Images.Media.IS_PENDING, true);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "screenshot.png");

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        values.put(MediaStore.Images.Media.IS_PENDING, false);
                        getContentResolver().update(uri, values, null, null);
                        Toast.makeText(this, "Screenshot saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                Log.e("QRCodeActivity", "Error saving screenshot", e);
                Toast.makeText(this, "Error saving screenshot", Toast.LENGTH_SHORT).show();
            }
        } else {
            // For Android versions below Q, save to external files directory
            File imagePath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.png");
            try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Toast.makeText(this, "Screenshot saved successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("QRCodeActivity", "Error saving screenshot", e);
                Toast.makeText(this, "Error saving screenshot", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generateQRCode(String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            imageViewQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkStoragePermission() {
        // Only check for permission on Android versions below Q (API level 29)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        // Request permission only if it's necessary (Android versions below Q)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}