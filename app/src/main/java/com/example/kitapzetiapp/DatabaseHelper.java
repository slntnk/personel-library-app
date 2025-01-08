package com.example.kitapzetiapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YeniKitaplar";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_KITAPLAR = "kitaplar";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_KITAP_ADI = "kitapAdi";
    private static final String COLUMN_KITAP_YAZARI = "kitapYazari";
    private static final String COLUMN_KITAP_OZETI = "kitapOzeti";
    private static final String COLUMN_KITAP_RESIM = "kitapResim";
    private static final String COLUMN_TARIH = "tarih";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "kitapAdi TEXT, " +
                "kitapYazari TEXT, " +
                "kitapOzeti TEXT, " +
                "kitapResim BLOB, " +
                "tarih TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KITAPLAR);
        onCreate(db);
    }

    public void kitapEkle(Kitap kitap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_KITAP_ADI, kitap.getKitapAdi());
        values.put(COLUMN_KITAP_YAZARI, kitap.getKitapYazari());
        values.put(COLUMN_KITAP_OZETI, kitap.getKitapOzeti());
        values.put(COLUMN_KITAP_RESIM, kitap.getKitapResim());
        values.put(COLUMN_TARIH, kitap.getTarih());

        db.insert(TABLE_KITAPLAR, null, values);
        db.close();
    }

    public ArrayList<Kitap> tumKitaplariGetir() {
        ArrayList<Kitap> kitapList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_KITAPLAR;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String kitapAdi = cursor.getString(cursor.getColumnIndex(COLUMN_KITAP_ADI));
                String kitapYazari = cursor.getString(cursor.getColumnIndex(COLUMN_KITAP_YAZARI));
                String kitapOzeti = cursor.getString(cursor.getColumnIndex(COLUMN_KITAP_OZETI));
                byte[] kitapResim = cursor.getBlob(cursor.getColumnIndex(COLUMN_KITAP_RESIM));
                String tarih = cursor.getString(cursor.getColumnIndex(COLUMN_TARIH));

                Kitap kitap = new Kitap(id, kitapAdi, kitapYazari, kitapOzeti, tarih, kitapResim);
                kitapList.add(kitap);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return kitapList;
    }
}
