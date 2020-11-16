package com.this25.safeteria.RegisterActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.this25.safeteria.LoginActivity.LoginActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.WriteActivity.WriteActivity;
import com.this25.safeteria.WriteActivity.Write_data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Store_RegisterActivity extends AppCompatActivity {
    Button ok, menu_add, photo_btn_add, service_choice;
    ImageView main_photo, store_on_off;
    EditText store_bar_edit, homepage, order, area;
    Button time_1, time_2;
    int hour = 0 ,minutes = 0;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private ArrayList<String> pathList = new ArrayList<>();
    private int pathCount, successCount;
    private Uri filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_store);
        //서비스를 위한
        final List<String> list = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        homepage = findViewById(R.id.homepage);
        order = findViewById(R.id.order);

        store_on_off = findViewById(R.id.store_on_off);
        main_photo = findViewById(R.id.main_photo);

        area = findViewById(R.id.area);

        time_1 = findViewById(R.id.time1);
        time_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Store_RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour = hourOfDay;
                        minutes = minute;
                        time_1.setText(String.format("%d:%d",hour, minutes));
                    }
                }, hour, minutes, false);
                timePickerDialog.show();
            }
        });
        time_2 = findViewById(R.id.time2);
        time_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Store_RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour = hourOfDay;
                                minutes = minute;
                                time_2.setText(String.format("%d:%d",hour, minutes));
                            }
                        }, hour, minutes, false);
                timePickerDialog.show();
            }
        });

        photo_btn_add = (Button)findViewById(R.id.photo_bar_btn);
        photo_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        menu_add = findViewById(R.id.menu_add_btn);
        menu_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu_Add_Dialog menu_add_dialog = new Menu_Add_Dialog(Store_RegisterActivity.this);
                menu_add_dialog.callFunction();
                if(user == null) {
                    myStartActivity(LoginActivity.class);
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("managers").document(user.getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(document != null) {
                                    if(document.exists()) {

                                    } else {
                                        myStartActivity(RegisterActivity.class);
                                    }
                                }
                            } else {

                            }
                        }
                    });
                    db.collection("store")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<Menu_Info> postList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                            postList.add(new Menu_Info(
                                                    document.getData().get("menu_name").toString(),
                                                    document.getData().get("menu_menual").toString(),
                                                    document.getData().get("menu_price").toString()
                                            ));
                                        }
                                        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.menu_add_view);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
//                                    @Override
//                                    public boolean canScrollVertically() {
//                                        return false;
//                                    }
//                                };
//                                recyclerView.setLayoutManager(linearLayoutManager);

                                        RecyclerView.Adapter mAdapter = new MenuAdapter(Store_RegisterActivity.this, postList);
                                        recyclerView.setAdapter(mAdapter);
                                    } else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });

        service_choice = findViewById(R.id.service_choise);
        service_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"포장", "배달", "주차", "반려동물", "24시"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(Store_RegisterActivity.this);
                dialog  .setTitle("서비스 선택")
                        .setMultiChoiceItems(
                                items
                                , new boolean[]{false, false, false, false, false}
                                , new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if(isChecked) {
                                            list.add(items[which]);
                                        } else {
                                            list.remove(items[which]);
                                        }
                                    }
                                }
                        ).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedItem = "";
                                for(String item : list) {
                                    selectedItem += item + "/";
                                    service_choice.setText(selectedItem);
                                }
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.create();
                dialog.show();
            }
        });

        ok = findViewById(R.id.ok);
        store_bar_edit = findViewById(R.id.store_bar_edit);
        store_bar_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력하기 전
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
                            Intent intent = new Intent(getApplicationContext(), Manager_RegisterActivity.class);
                            startActivity(intent);
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
                //입력이 끝났을 때

            }
        });
    }
//    public void DataSave() {
//        String filename = "profile.png";
//        String cu = mAuth.getUid();
//        final String store_name = store_bar_edit.getText().toString(); //가게 이름
//        final String time1 = time_1.getText().toString(); //이용 시간 전
//        final String time2 = time_2.getText().toString(); //이용 시간 후
//        final String service = service_choice.getText().toString(); //서비스
//        final String homepages = homepage.getText().toString(); //홈페이지
//        final String notices = order.getText().toString(); //order 공지사함
//        final String main_photoUri = filename; //대표 사진
//        if(store_name.length() > 0) { {
//
//        }}
//        if (filePath == null) {
//            Toast.makeText(getApplicationContext(), "파일 선택 후 내용을 작성해 주세요.", Toast.LENGTH_SHORT).show();
//        } else {
//            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("Manager").child(cu).child("images/profile.png");
//            storageRef.putFile(filePath)//성공시
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    //        Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    //실패시
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                    //        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    //진행중
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        }
//                    });
//            FirebaseUser user = mAuth.getCurrentUser();
//
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//
//            Log.v("알림", "현재로그인한 유저 " + cu);
//            Log.v("알림", "현재로그인한 이메일 " + email);
//            Log.v("알림", "유저 이름 " + name);
//            Log.v("알림", "가게 이름 " + store_name);
//            Log.v("알림", "이용 시간 " + time1 + " ~ " + time2);
//            Log.v("알림", "가게 서비스" + service);
//            Log.v("알림", "가게 홈페이지 " + homepages);
//            Log.v("알림", "가게 공지사항 " + notices);
//            Log.v("알림", "가게 대표사진 " + main_photoUri);
//            //이 부분이 DB에 데이터 저장
//            Manager_Info managerInfo = new Manager_Info(store_name, time1, time2, service, homepages, notices, main_photoUri);
//            //Menu_Info menuInfo = new Menu_Info();
//            mDatabase.getReference().child("Manager_Info").child(cu).setValue(managerInfo)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Write was successful!
//                     //       Toast.makeText(Store_RegisterActivity.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Write failed
//                     //       Toast.makeText(Store_RegisterActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
    private void storageUpload() {
        FirebaseUser user = mAuth.getCurrentUser();
        String cu = mAuth.getUid();
        final String store_name = store_bar_edit.getText().toString(); //가게 이름
        final String time1 = time_1.getText().toString(); //이용 시간 전
        final String time2 = time_2.getText().toString(); //이용 시간 후
        final String service = service_choice.getText().toString(); //서비스
        final String homepages = homepage.getText().toString(); //홈페이지
        final String notices = order.getText().toString(); //order 공지사함
        final String areas = area.getText().toString();
        //final String main_photoUri = filename; //대표 사진

//        final RelativeLayout loderLayout = findViewById(R.id.loaderLayout);
//        loderLayout.setVisibility(View.VISIBLE);

        //final String date_tv = time;

        if(store_name.length() > 0 && filePath != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now);
            final ArrayList<String> contentList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("Manager").child(cu).child("images/" + "profile.png");
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
            if (pathList.size() == 0) {
                //loderLayout.setVisibility(View.GONE);
                Manager_Info managerInfo = new Manager_Info(store_name, time1, time2, service, homepages, notices, "main_photoUri", areas);
                storeUpload(managerInfo);
            } else {

            }
        }
    }
    private void storeUpload(Manager_Info manager_info) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db.collection("/managers").document(user.getUid()).set(manager_info)
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

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("알림/ ", "대표사진 uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                main_photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}
