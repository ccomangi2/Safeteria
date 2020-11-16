package com.this25.safeteria.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.this25.safeteria.PostActivity.User_PostActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.WriteActivity.Write_data;

import java.util.ArrayList;

public class ReviewAdpater extends RecyclerView.Adapter<ReviewAdpater.GalleryViewHolder> {
    private ArrayList<Write_data> mDataset;
    private Activity activity;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewAdpater(Activity activity, ArrayList<Write_data> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ReviewAdpater.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, User_PostActivity.class);
                //intent.putExtra("posts", mDataset.get(galleryViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        return galleryViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView store_name = cardView.findViewById(R.id.store_name);
        store_name.setText(mDataset.get(position).getStore());

        TextView user_name = cardView.findViewById(R.id.user_name);
        user_name.setText(mDataset.get(position).getUser_nickname());

        TextView write_date = cardView.findViewById(R.id.write_date);
        write_date.setText(mDataset.get(position).getDate());

        TextView write = cardView.findViewById(R.id.write);
        write.setText(mDataset.get(position).getWrite());

        TextView like_count = cardView.findViewById(R.id.like_count);
        like_count.setText(mDataset.get(position).getRating());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}