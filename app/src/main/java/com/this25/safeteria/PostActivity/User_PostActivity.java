package com.this25.safeteria.PostActivity;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.this25.safeteria.LoginActivity.LoginActivity;
import com.this25.safeteria.ProfileActivity.ProfileActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.RegisterActivity;
import com.this25.safeteria.WriteActivity.WriteActivity;
import com.this25.safeteria.WriteActivity.Write_data;

import java.util.ArrayList;

public class User_PostActivity extends AppCompatActivity {
    RatingBar ratingBar;
    TextView store, name, data, write_tv, rating_tv;
    ImageView review_img, profile;

    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);
        ratingBar = findViewById(R.id.ratingBar);
        store = findViewById(R.id.store_tv);
        name = findViewById(R.id.user_name);
        data = findViewById(R.id.data);
        write_tv = findViewById(R.id.write_tv);
        rating_tv = findViewById(R.id.rating_tv);
        review_img = findViewById(R.id.write_img);
        profile = findViewById(R.id.account_iv_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String cu = user.getUid();
        if (user == null) {
            myStartActivity(LoginActivity.class);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("TAG", "No such document");
                                myStartActivity(RegisterActivity.class);
                            }
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });

            db.collection("posts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Write_data> postList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                    postList.add(new Write_data(
                                            document.getData().get("store").toString(),
                                            document.getData().get("write").toString(),
                                            document.getData().get("date").toString(),
                                            document.getData().get("user").toString(),
                                            document.getData().get("rating").toString(),
                                            document.getData().get("rating").toString(),
                                            document.getData().get("user_nickname").toString()
                                            //document.getData().get("like_count").toString()
                                    ));
                                    name.setText(document.getData().get("user_nickname").toString());
                                    store.setText(document.getData().get("store").toString());
                                    data.setText(document.getData().get("date").toString());
                                    write_tv.setText(document.getData().get("write").toString());
                                    rating_tv.setText(document.getData().get("rating").toString());
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            String filename = "profile.png";
            //FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("images/profile.png");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(User_PostActivity.this)
                            .load(uri)
                            .into(profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                }
            });

            StorageReference post = FirebaseStorage.getInstance().getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("Review/img" + "profile.png");
            post.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(User_PostActivity.this)
                            .load(uri)
                            .into(review_img);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}