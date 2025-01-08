package com.example.kitapzetiapp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class KitapDetayiActivity extends AppCompatActivity {

    private TextView textViewOzeti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitap_detay);

        ImageView imageView = findViewById(R.id.imageViewDetay);
        TextView textViewAdi = findViewById(R.id.textViewKitapAdiDetay);
        TextView textViewYazari = findViewById(R.id.textViewKitapYazariDetay);
        textViewOzeti = findViewById(R.id.textViewKitapOzetiDetay);
        TextView textViewTarih = findViewById(R.id.textViewTarihDetay);
        TextView textViewOzetDuzenle = findViewById(R.id.textViewOzetDuzenle);

        // Gelen intent verileri
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String kitapAdi = extras.getString("kitapAdi");
            String kitapYazari = extras.getString("kitapYazari");
            String kitapOzeti = extras.getString("kitapOzeti");
            String tarih = extras.getString("tarih");
            byte[] kitapResim = extras.getByteArray("kitapResim");
            int kitapId = extras.getInt("kitapId", -1);

            textViewAdi.setText(kitapAdi);
            textViewYazari.setText(kitapYazari);
            textViewOzeti.setText(kitapOzeti);
            textViewTarih.setText(tarih);

            if (kitapResim != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(kitapResim, 0, kitapResim.length);
                imageView.setImageBitmap(bitmap);
            }


            textViewOzetDuzenle.setOnClickListener(v -> {
                if (kitapId != -1 && kitapOzeti != null) {
                    showEditOzetDialog(kitapId, kitapOzeti);
                } else {
                    Toast.makeText(this, "Kitap bilgisi yok", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void showEditOzetDialog(int kitapId, String mevcutOzet) {
        // AlertDialog için bir EditText oluşturuyoruz
        EditText editText = new EditText(this);
        editText.setText(mevcutOzet); // Mevcut özeti düzenlemek için EditText'e yerleştiriyoruz
        editText.setSelection(mevcutOzet.length()); // İmleci metnin sonuna yerleştriyoruz

        // AlertDialog  oluşturuyoruz
        new AlertDialog.Builder(this)
                .setTitle("Kitap Özeti Düzenle")
                .setView(editText)
                .setPositiveButton("Kaydet", (dialog, which) -> {
                    String yeniOzet = editText.getText().toString().trim();
                    if (yeniOzet.isEmpty()) {
                        Toast.makeText(this, "Özet boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Veritabanını güncelle
                    try {
                        SQLiteDatabase database = openOrCreateDatabase("YeniKitaplar", MODE_PRIVATE, null);
                        String sql = "UPDATE kitaplar SET kitapOzeti = ? WHERE id = ?";
                        SQLiteStatement statement = database.compileStatement(sql);
                        statement.bindString(1, yeniOzet);
                        statement.bindLong(2, kitapId);
                        statement.execute();

                        Toast.makeText(this, "Kitap özeti başarıyla güncellendi!", Toast.LENGTH_SHORT).show();

                        // Güncellenen özeti TextView'e yerleştiriyoruz
                        textViewOzeti.setText(yeniOzet);

                        setResult(RESULT_OK);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Güncelleme sırasında bir hata oluştu!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
    }
}
