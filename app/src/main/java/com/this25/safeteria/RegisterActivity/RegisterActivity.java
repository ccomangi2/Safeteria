package com.this25.safeteria.RegisterActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.this25.safeteria.LoginActivity.UserData;
import com.this25.safeteria.MainActivity.MainActivity;
import com.this25.safeteria.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    // DatabaseReference 는 데이터베이스에서 데이터를 읽고 쓰려면 꼭 필요
    DatabaseReference conditionRdf = mRoootRef.child("text");

    Button manager_btn;
    Button user_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Authentication, Database, Storage 초기화
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //관리자용
        manager_btn = findViewById(R.id.manager_btn);
        manager_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = "Manager";

                HashMap result = new HashMap<>();
                result.put("name", user);

                storageUpload(user);

                Intent intent = new Intent(getApplicationContext(), Manager_RegisterActivity.class);
                startActivity(intent);
            }
        });

        //사용자용
        user_btn = findViewById(R.id.user_btn);
        user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = "User";

                HashMap result = new HashMap<>();
                result.put("name", user);

                storageUpload(user);

                Intent intent = new Intent(getApplicationContext(), User_RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private void storageUpload(String usersave) {
        FirebaseUser user = mAuth.getCurrentUser();

        String cu = mAuth.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();
        String photoUrl = user.getPhotoUrl().toString();
        String phone = user.getPhoneNumber();

        Log.v("알림", "현재로그인한 유저 " + cu);
        Log.v("알림", "현재로그인한 이메일 " + email);
        Log.v("알림", "유저 이름 " + name);
        Log.v("알림", "유저 사진 " + photoUrl);
        Log.v("알림", "유저 폰 " + phone);

        //final String date_tv = time;

        if(usersave.length() > 0) {
            UserData userdata = new UserData(usersave, name, cu, email, photoUrl, phone);
            storeUpload(userdata, usersave);
        }
    }
    private void storeUpload(UserData userdata, String usersave) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db.collection(usersave).document(user.getUid()).set(userdata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("알림", "DocumentSnapshot added with ID: " + d.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("알림", "Error adding document", e);
                    }
                });
    }
}
