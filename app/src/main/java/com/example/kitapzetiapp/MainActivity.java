package com.example.kitapzetiapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView; //kitap listesi bileşeni
    private KitapAdapter kitapAdapter; // kitapList ile recylerview ı bağlar
    private ArrayList<Kitap> kitapList; //kitapları depolayan liste
    private ActivityResultLauncher<Intent> kitapDetayLauncher; //kitap özeti düzenlendikten sonra dönen sonucu işler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        kitapDetayLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        kitaplariGetir(); // aktivite degisiminde kitap listesi yenilenir
                    }
                }
        );

        recyclerView = findViewById(R.id.recyclerView);
        kitapList = new ArrayList<>();
        kitapAdapter = new KitapAdapter(kitapList, this, kitapDetayLauncher);

        // RecyclerView ayarları
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 sütunlu bir grid düzeni
        recyclerView.setLayoutManager(gridLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration); //liste öğelerinin arasına dikey çizgi eklenmesi

        recyclerView.setAdapter(kitapAdapter); //recyclerview a adaptör bağlanır

        kitaplariGetir();
    }

    private void kitaplariGetir() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        kitapList.clear();
        kitapList.addAll(dbHelper.tumKitaplariGetir());
        kitapAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yeni_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.yeni_menu_yeni_kitap) {
            Intent kitapEkleIntent = new Intent(this, YeniKitapActivity.class);
            startActivity(kitapEkleIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
