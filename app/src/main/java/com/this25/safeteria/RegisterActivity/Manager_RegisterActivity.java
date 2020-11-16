package com.this25.safeteria.RegisterActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.this25.safeteria.LoginActivity.LoginActivity;
import com.this25.safeteria.LoginActivity.UserData;
import com.this25.safeteria.MainActivity.MainActivity;
import com.this25.safeteria.MainActivity.MainActivity_Manager;
import com.this25.safeteria.ProfileActivity.User_ProfileEditActivity;
import com.this25.safeteria.R;

import java.util.ArrayList;

public class Manager_RegisterActivity extends AppCompatActivity {
    Button ok;
    ImageButton back;
    Button store_choice;
    ImageView account_iv_profile;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    // DatabaseReference 는 데이터베이스에서 데이터를 읽고 쓰려면 꼭 필요
    DatabaseReference conditionRdf = mRoootRef.child("text");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_register);
        account_iv_profile = findViewById(R.id.account_iv_profile);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String cu = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                myStartActivity(LoginActivity.class);
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("/managers").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {

                                } else {
                                    //myStartActivity(RegisterActivity.class);
                                }
                            }
                        } else {

                        }
                    }
                });
                db.collection("/managers").document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    ArrayList<Manager_Info> postList = new ArrayList<>();
                                    if (document.exists()) {
                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                        postList.add(new Manager_Info(
                                                document.getData().get("store_name").toString(),
                                                document.getData().get("time1").toString(),
                                                document.getData().get("time2").toString(),
                                                document.getData().get("service").toString(),
                                                document.getData().get("homepage").toString(),
                                                document.getData().get("notice").toString(),
                                                document.getData().get("main_photoUri").toString(),
                                                document.getData().get("area").toString()
                                        ));
                                        store_choice.setText(document.getData().get("store_name").toString());
                                    }
                                    else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });
            }

            String filename = "profile.png";
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("Manager").child(cu).child("images/" + "profile.png");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(Manager_RegisterActivity.this)
                            .load(uri)
                            .into(account_iv_profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Toast.makeText(Manager_RegisterActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

        }

        //뒤로가기
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //가게등록
        store_choice = (Button)findViewById(R.id.store_choice_btn);
        store_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Store_RegisterActivity.class);
                startActivity(intent);
            }
        });

        //완료
        ok = findViewById(R.id.ok);
        store_choice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력하기 전
                String text = s.toString();
                if(text.length() == 0) {
                    ok.setBackgroundResource(R.drawable.dhksfy);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력의 변화가 있을 때
                String text = s.toString();
                if(text.length() >= 4) {
                    ok.setBackgroundResource(R.drawable.dhksfy_on);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity_Manager.class);
                            startActivity(intent);
                        }
                    });
                } else if(text.length() < 4) {
                    ok.setBackgroundResource(R.drawable.dhksfy);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력이 끝났을 때
                String text = s.toString();
                if(text.length() >= 4) {
                    ok.setBackgroundResource(R.drawable.dhksfy_on);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    ok.setBackgroundResource(R.drawable.dhksfy_on);
                }
            }
        });
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}
