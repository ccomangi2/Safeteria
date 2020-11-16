package com.this25.safeteria.SettingActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.this25.safeteria.MainActivity.MainActivity;
import com.this25.safeteria.ProfileActivity.ProfileActivity;
import com.this25.safeteria.R;

public class SettingActivity extends AppCompatActivity {
    ImageButton back;
    Button service_center_btn;
    ImageView account_iv_profile;
    TextView user_name, user_space, user_space2;

    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        account_iv_profile = findViewById(R.id.account_iv_profile);
        user_name = findViewById(R.id.user_name);
        user_space = findViewById(R.id.user_space);
        user_space2 = findViewById(R.id.user_space2);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String cu = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mRoootRef.child("User_Info").child(cu).child("nickname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user = dataSnapshot.getValue(String.class);
                    user_name.setText(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            mRoootRef.child("User_Info").child(cu).child("area").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user = dataSnapshot.getValue(String.class);
                    user_space.setText(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            mRoootRef.child("User_Info").child(cu).child("spinner_do").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user = dataSnapshot.getValue(String.class);
                    user_space2.setText(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            String filename = "profile.png";
            //FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("images/profile.png");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(SettingActivity.this)
                            .load(uri)
                            .into(account_iv_profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Toast.makeText(getApplicationContext(), "실패.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

        }

        //뒤로가기
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //고객센터
        service_center_btn = findViewById(R.id.service_center);
        service_center_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting_Service_CenterActivity.class);
                startActivity(intent);
            }
        });

    }
}
