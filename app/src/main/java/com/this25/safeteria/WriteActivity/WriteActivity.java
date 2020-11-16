package com.this25.safeteria.WriteActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.this25.safeteria.MainActivity.MainActivity;
import com.this25.safeteria.PostActivity.User_PostActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.Manager_RegisterActivity;
import com.this25.safeteria.RegisterActivity.User_Info;
import com.this25.safeteria.RegisterActivity.User_RegisterActivity;
import com.this25.safeteria.ReviewActivity.ReviewActivity;
import com.this25.safeteria.activity.GalleryActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    ImageButton back;
    Button ok;
    RatingBar ratingBar;
    EditText write_edit, store_bar_edit;
    ImageView write_img, store_on_off;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    private  LinearLayout parant;
    private int pathCount, successCount;
    private Uri filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        ratingBar = findViewById(R.id.ratingBar1);
        ok = findViewById(R.id.ok);
        write_edit = findViewById(R.id.write_edit);
        store_bar_edit = findViewById(R.id.store_bar_edit);
        write_img = findViewById(R.id.write_img);
        store_on_off = findViewById(R.id.store_on_off);

        parant = findViewById(R.id.contentsLayout);

        FloatingActionButton fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(new FABClickListener());

        //뒤로가기
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //tv.setText("rating : " + rating);
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = s.toString();
                if(text.length() == 0) {
                    store_on_off.setImageResource(R.drawable.store_ok);
                    ok.setTextColor(Color.GRAY);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "내용 작성을 완료해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력의 변화가 있을 때
                String strColor = "#2EC0FA";
                String text = s.toString();
                if(text.length() != 0) {
                    store_on_off.setImageResource(R.drawable.which_small);
                    ok.setTextColor(Color.parseColor(strColor));
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            storageUpload();
                            myStartActivity(User_PostActivity.class);
                        }
                    });
                } else {
                    store_on_off.setImageResource(R.drawable.store_ok);
                    ok.setTextColor(Color.GRAY);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "내용 작성을 완료해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        store_bar_edit.addTextChangedListener(textWatcher);
        write_edit.addTextChangedListener(textWatcher);
    }
    class FABClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
        }
    }
    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("알림/ ", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                write_img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void storageUpload() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd");
        final String time = mFormat.format(date);
        final String title = ((EditText)findViewById(R.id.store_bar_edit)).getText().toString();
        final String contents = ((EditText)findViewById(R.id.write_edit)).getText().toString();
        final TextView rating_tv = findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_tv.setText("1" + rating);
            }
        });

        //final String date_tv = time;

        if(title.length() > 0 && contents.length() > 0 && filePath != null) {
            final ArrayList<String> contentList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            String cu = user.getUid();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("Review/img" + "profile.png");
            storageRef.putFile(filePath)//성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //   Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //   Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
            if(pathList.size() == 0) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                            Write_data write_data = new Write_data(title, contents, time, user.getUid(),rating_tv.getText().toString(), "", document.getData().get("nickname").toString());
                                            storeUpload(write_data);
                                        } else {
                                            Log.d("TAG", "No such document");
                                        }
                                    } else {
                                        Log.d("TAG", "get failed with ", task.getException());
                                    }
                                }
                            }
                        });
            }
        } else {

        }
    }

    private void storeUpload(Write_data write_data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(write_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("알림", "DocumentSnapshot added with ID: " + documentReference.getId());
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
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}