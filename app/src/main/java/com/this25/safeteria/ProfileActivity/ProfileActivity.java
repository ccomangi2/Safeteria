package com.this25.safeteria.ProfileActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.Manager_Info;
import com.this25.safeteria.RegisterActivity.RegisterActivity;
import com.this25.safeteria.RegisterActivity.User_Info;
import com.this25.safeteria.WriteActivity.Write_data;
import com.this25.safeteria.adapter.ReviewAdpater;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfileActivity extends Fragment {
    private Button modify;
    private ImageView profile_image;
    private TextView user_name;
    private View view;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private Uri filePath;


    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    // DatabaseReference 는 데이터베이스에서 데이터를 읽고 쓰려면 꼭 필요
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile, null);

        profile_image = view.findViewById(R.id.account_iv_profile);
        user_name = view.findViewById(R.id.user_name);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String cu = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                myStartActivity(LoginActivity.class);
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("/users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {

                                } else {
                                    myStartActivity(RegisterActivity.class);
                                }
                            }
                        } else {

                        }
                    }
                });
                db.collection("/users").document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        ArrayList<User_Info> postList = new ArrayList<>();
                                        if (document.exists()) {
                                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                            postList.add(new User_Info(
                                                    document.getData().get("userName").toString(),
                                                    document.getData().get("userId").toString(),
                                                    document.getData().get("email").toString(),
                                                    document.getData().get("nickname").toString(),
                                                    document.getData().get("area").toString(),
                                                    document.getData().get("spinner_do").toString(),
                                                    document.getData().get("photoUrl").toString()
                                            ));
                                            user_name.setText(document.getData().get("nickname").toString());
                                        } else {
                                            Log.d("TAG", "No such document");
                                        }
                                    } else {
                                        Log.d("TAG", "get failed with ", task.getException());
                                    }
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
                                        if(user_name.getText().toString().equals(document.getData().get("user_nickname").toString())) {
                                            RecyclerView recyclerView = view.findViewById(R.id.my_review_view);
                                            recyclerView.setHasFixedSize(true);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
//                                    @Override
//                                    public boolean canScrollVertically() {
//                                        return false;
//                                    }
//                                };
//                                recyclerView.setLayoutManager(linearLayoutManager);
                                            RecyclerView.Adapter mAdapter = new ReviewAdpater(getActivity(), postList);
                                            recyclerView.setAdapter(mAdapter);
                                        }
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }

            String filename = "profile.png";
            //FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("images/profile.png");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .into(profile_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Toast.makeText(getActivity(), "실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

        }

        modify = view.findViewById(R.id.modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), User_ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
        private void myStartActivity(Class c) {
            Intent intent = new Intent(getContext(), c);
            startActivityForResult(intent, 0);
        }
}
