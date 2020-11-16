package com.this25.safeteria.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.this25.safeteria.HomeActivity.HomeActivity;
import com.this25.safeteria.ProfileActivity.ProfileActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.Manager_Info;

import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.GalleryViewHolder> {
    private ArrayList<Manager_Info> mDataset;
    private Activity activity;
    private FirebaseAuth mAuth;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StoreAdapter(Activity activity, ArrayList<Manager_Info> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }


    @NonNull
    @Override
    public StoreAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        final StoreAdapter.GalleryViewHolder galleryViewHolder = new StoreAdapter.GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.GalleryViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        final ImageView store_img = cardView.findViewById(R.id.store_img);
        mAuth = FirebaseAuth.getInstance();
        String cu = mAuth.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("Manager").child(cu).child("images/" + "profile.png");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지 로드 성공시
                Glide.with(cardView.getContext())
                        .load(uri)
                        .into(store_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(cardView.getContext(), "실패", Toast.LENGTH_SHORT).show();
            }
        });


        TextView store_name = cardView.findViewById(R.id.store_name);
        store_name.setText(mDataset.get(position).getStore_name());

        TextView store_area = cardView.findViewById(R.id.store_area);
        store_area.setText(mDataset.get(position).getArea());

        TextView rating = cardView.findViewById(R.id.rating);
        rating.setText(mDataset.get(position).getRating());
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
