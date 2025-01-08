package com.example.kitapzetiapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class YeniKitapActivity extends AppCompatActivity {

    private EditText editTextKitapIsmi, editTextKitapYazari, editTextKitapOzet;
    private TextView textViewTarih;
    private ImageView imgKitapResim;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private byte[] selectedImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_kitap);

        editTextKitapIsmi = findViewById(R.id.editTextText);
        editTextKitapYazari = findViewById(R.id.editTextText2);
        editTextKitapOzet = findViewById(R.id.editTextText3);
        imgKitapResim = findViewById(R.id.imageView);
        textViewTarih = findViewById(R.id.textViewDate);

        // Tarih seçiciye tıklama olayı
        textViewTarih.setOnClickListener(v -> showDatePickerDialog());

        // Resim seçici başlatıcı
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImage);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            }

                            imgKitapResim.setImageBitmap(bitmap);
                            selectedImageBytes = convertBitmapToBytes(bitmap);
                            Toast.makeText(this, "Resim başarıyla seçildi.", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Resim seçilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    // Tarih seçici
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    textViewTarih.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    // Kaydetme işlemi
    public void kaydet(View v) {
        String kitapIsmi = editTextKitapIsmi.getText().toString().trim();
        String kitapYazari = editTextKitapYazari.getText().toString().trim();
        String kitapOzeti = editTextKitapOzet.getText().toString().trim();
        String tarih = textViewTarih.getText().toString().trim();

        if (kitapIsmi.isEmpty() || kitapYazari.isEmpty() || kitapOzeti.isEmpty() ||(tarih.isEmpty() || tarih.equals("Tarih:")) || selectedImageBytes == null) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kitap nesnesi oluşturuluyor
        Kitap yeniKitap = new Kitap(0, kitapIsmi, kitapYazari, kitapOzeti, tarih, selectedImageBytes);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.kitapEkle(yeniKitap);

        Toast.makeText(this, "Kitap başarıyla kaydedildi!", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Resim seçme işlemi
    public void resimSec(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                openImagePicker();
            }
        }
    }

    // Galeri açılır
    private void openImagePicker() {
        Intent resmiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(resmiAl);
    }

    // Bitmap'i byte array'e çevirme
    private byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return outputStream.toByteArray();
    }

    // İzin sonuçlarını kontrol etme
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "İzin reddedildi!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
