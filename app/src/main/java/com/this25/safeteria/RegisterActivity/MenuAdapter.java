package com.this25.safeteria.RegisterActivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.this25.safeteria.R;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.GalleryViewHolder> {
    private ArrayList<Menu_Info> mDataset;
    private Activity activity;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MenuAdapter(Activity activity, ArrayList<Menu_Info> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MenuAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        final MenuAdapter.GalleryViewHolder galleryViewHolder = new MenuAdapter.GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView store_img = cardView.findViewById(R.id.menu_drwable);

        TextView menu_name = cardView.findViewById(R.id.menu_name);
        menu_name.setText(mDataset.get(position).getMenu_name());

        TextView menu_menual = cardView.findViewById(R.id.menu_menual);
        menu_menual.setText(mDataset.get(position).getMenu_menual());

        TextView menu_price = cardView.findViewById(R.id.menu_price);
        menu_price.setText(mDataset.get(position).getMenu_price());

        Glide.with(activity).load(mDataset.get(position)).centerCrop().override(1000).into(store_img);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
