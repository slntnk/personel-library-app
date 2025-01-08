package com.example.kitapzetiapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KitapAdapter extends RecyclerView.Adapter<KitapAdapter.KitapViewHolder> {

    private ArrayList<Kitap> kitapList;
    private Context context;
    private ActivityResultLauncher<Intent> kitapDetayLauncher;

    public KitapAdapter(ArrayList<Kitap> kitapList, Context context, ActivityResultLauncher<Intent> kitapDetayLauncher) {
        this.kitapList = kitapList;
        this.context = context;
        this.kitapDetayLauncher = kitapDetayLauncher;
    }

    @NonNull
    @Override
    public KitapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.kitap_item, parent, false);
        return new KitapViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KitapViewHolder holder, int position) {
        Kitap currentKitap = kitapList.get(position);

        holder.textViewKitapAdi.setText(currentKitap.getKitapAdi());
        holder.textViewKitapYazari.setText(currentKitap.getKitapYazari());

        byte[] kitapResim = currentKitap.getKitapResim();
        if (kitapResim != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(kitapResim, 0, kitapResim.length);
            holder.imageViewKitap.setImageBitmap(bitmap);
        }

        holder.itemView.setOnClickListener(v -> {      //tıklama olayı
            Intent intent = new Intent(context, KitapDetayiActivity.class);
            intent.putExtra("kitapId", currentKitap.getId());
            intent.putExtra("kitapAdi", currentKitap.getKitapAdi());
            intent.putExtra("kitapYazari", currentKitap.getKitapYazari());
            intent.putExtra("kitapOzeti", currentKitap.getKitapOzeti());
            intent.putExtra("tarih", currentKitap.getTarih());
            intent.putExtra("kitapResim", currentKitap.getKitapResim());

            kitapDetayLauncher.launch(intent); // Kitap detayına geçiş
        });

        holder.itemView.setOnLongClickListener(v -> {  // uzun tıklama olayı
            new AlertDialog.Builder(context)
                    .setTitle("Kitap Silme")
                    .setMessage("'" + currentKitap.getKitapAdi() + "' adlı kitabı silmek istiyor musunuz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        kitapSil(currentKitap.getId());
                        kitapList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, kitapList.size());
                        Toast.makeText(context, "Kitap silindi.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
            return true;
        });
    }

    private void kitapSil(int kitapId) {
        SQLiteDatabase database = context.openOrCreateDatabase("YeniKitaplar", Context.MODE_PRIVATE, null);
        String sql = "DELETE FROM kitaplar WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindLong(1, kitapId);
        statement.execute();
        database.close();
    }

    @Override
    public int getItemCount() {
        return kitapList.size();
    }

    public static class KitapViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewKitap;
        TextView textViewKitapAdi;
        TextView textViewKitapYazari;

        public KitapViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewKitap = itemView.findViewById(R.id.imageViewKitap);
            textViewKitapAdi = itemView.findViewById(R.id.textViewKitapAdi);
            textViewKitapYazari = itemView.findViewById(R.id.textViewKitapYazari);
        }
    }
}
