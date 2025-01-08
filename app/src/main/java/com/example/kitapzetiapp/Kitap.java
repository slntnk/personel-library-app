package com.example.kitapzetiapp;

public class Kitap {
    private int id;
    private String kitapAdi;
    private String kitapYazari;
    private String kitapOzeti;
    private String tarih;
    private byte[] kitapResim;


    public Kitap(int id, String kitapAdi, String kitapYazari, String kitapOzeti, String tarih, byte[] kitapResim) {
        this.id = id;
        this.kitapAdi = kitapAdi;
        this.kitapYazari = kitapYazari;
        this.kitapOzeti = kitapOzeti;
        this.tarih = tarih;
        this.kitapResim = kitapResim;
    }

    public int getId() {
        return id;
    }

    public String getKitapAdi() {
        return kitapAdi;
    }

    public String getKitapYazari() {
        return kitapYazari;
    }

    public String getKitapOzeti() {
        return kitapOzeti;
    }

    public String getTarih() {
        return tarih;
    }

    public byte[] getKitapResim() {
        return kitapResim;
    }
}
