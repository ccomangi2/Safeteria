package com.this25.safeteria.ProfileActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.this25.safeteria.LoginActivity.LoginActivity;
import com.this25.safeteria.MainActivity.MainActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.RegisterActivity;
import com.this25.safeteria.RegisterActivity.User_Info;
import com.this25.safeteria.RegisterActivity.User_RegisterActivity;

import java.io.IOException;
import java.util.ArrayList;

public class User_ProfileEditActivity extends AppCompatActivity {
    Spinner spin1;
    Spinner spin2;
    EditText nickname_edit;
    TextView check_tv;
    ImageView check, account_iv_profile;
    Button ok;
    ImageButton back, photo_add;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private Uri filePath;


    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    // DatabaseReference 는 데이터베이스에서 데이터를 읽고 쓰려면 꼭 필요
    DatabaseReference conditionRdf = mRoootRef.child("text");

    ArrayAdapter<CharSequence> adspin1, adspin2; //어댑터를 선언했습니다. adspint1(서울,인천..) adspin2(강남구,강서구..)
    String choice_do="";
    String choice_se="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofileedit);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        nickname_edit = findViewById(R.id.nickname_edit);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String cu = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                                            nickname_edit.setText(document.getData().get("nickname").toString());
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

            String filename = "profile.png";
            //FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("images/profile.png");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //이미지 로드 성공시
                    Glide.with(User_ProfileEditActivity.this)
                            .load(uri)
                            .into(account_iv_profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //이미지 로드 실패시
                    Toast.makeText(User_ProfileEditActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

        }

        //스피너
        spin1 = (Spinner)findViewById(R.id.spinner);
        spin2 = (Spinner)findViewById(R.id.spinner2);

        adspin1 = ArrayAdapter.createFromResource(this, R.array.spinner_do, R.layout.row_spinner);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin1.setAdapter(adspin1);
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adspin1.getItem(i).equals("서울")) {
                    choice_do = "서울";
                    adspin2 = ArrayAdapter.createFromResource(User_ProfileEditActivity.this, R.array.spinner_do_seoul, R.layout.row_spinner);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adspin1.getItem(i).equals("인천")) {
                    choice_do = "인천";
                    adspin2 = ArrayAdapter.createFromResource(User_ProfileEditActivity.this, R.array.spinner_do_incheon, R.layout.row_spinner);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adspin1.getItem(i).equals("광주")) {
                    choice_do = "광주";
                    adspin2 = ArrayAdapter.createFromResource(User_ProfileEditActivity.this, R.array.spinner_do_gwangju,R.layout.row_spinner);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adspin1.getItem(i).equals("대구")) {
                    choice_do = "대구";
                    adspin2 = ArrayAdapter.createFromResource(User_ProfileEditActivity.this, R.array.spinner_do_daegu, R.layout.row_spinner);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adspin1.getItem(i).equals("울산")) {
                    choice_do = "울산";
                    adspin2 = ArrayAdapter.createFromResource(User_ProfileEditActivity.this, R.array.spinner_do_ulsan, R.layout.row_spinner);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adspin1.getItem(i).equals("대전")) {
                    choice_do = "대전";
                    adspin2 = ArrayAdapter.createFromResource(User_ProfileEditActivity.this, R.array.spinner_do_daejeon, R.layout.row_spinner);
                    adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);
                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_se = adspin2.getItem(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //뒤로가기
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        photo_add = findViewById(R.id.photo_add);
        photo_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        check_tv = findViewById(R.id.check_tv);
        ok = findViewById(R.id.ok);
        check = findViewById(R.id.check);
        account_iv_profile = findViewById(R.id.account_iv_profile);

        nickname_edit = findViewById(R.id.nickname_edit);
        nickname_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력하기 전
                String text = s.toString();
                if(text.length() == 0) {
                    ok.setTextColor(Color.GRAY);
                    check_tv.setText(" ");
                    check.setImageResource(R.drawable.back_white);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력의 변화가 있을 때
                String strColor = "#2EC0FA";
                String text = s.toString();
                if(text.length() >= 4) {
                    check_tv.setText("사용할 수 있는 닉네임입니다.");
                    check_tv.setTextColor(Color.parseColor(strColor));
                    ok.setTextColor(Color.parseColor(strColor));
                    check.setImageResource(R.drawable.okok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DataSave(nickname_edit.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if(text.length() < 4) {
                    ok.setTextColor(Color.GRAY);
                    check.setImageResource(R.drawable.back_white);
                    check_tv.setText("4글자 이상을 입력해 주세요.");
                    check_tv.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력이 끝났을 때
                String strColor = "#2EC0FA";
                String text = s.toString();
                if(text.length() >= 4) {
                    check_tv.setText("사용할 수 있는 닉네임입니다.");
                    ok.setTextColor(Color.parseColor(strColor));
                    check.setImageResource(R.drawable.okok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DataSave(nickname_edit.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
    public void DataSave(String nickname) {
        if (filePath == null && nickname_edit.getText().toString().equals("") || nickname_edit.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "파일 선택 후 내용을 작성해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            //Date now = new Date();
            String filename = "profile.png";
            String cu = mAuth.getUid();
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("images/" + filename);
            storageRef.putFile(filePath)//성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
            FirebaseUser user = mAuth.getCurrentUser();

            String area = spin1.getSelectedItem().toString();
            String spinner_do = spin2.getSelectedItem().toString();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photoUrl = filename;

            Log.v("알림", "현재로그인한 유저 " + cu);
            Log.v("알림", "현재로그인한 이메일 " + email);
            Log.v("알림", "현재로그인한 유저 닉네임 " + nickname);
            Log.v("알림", "유저 이름 " + name);
            Log.v("알림", "유저 사진 " + photoUrl);
            Log.v("알림", "유저 지역 " + area);
            Log.v("알림", "유저 상세지역 " + spinner_do);

            //이 부분이 DB에 데이터 저장
            User_Info userInfo = new User_Info(name, cu, email, nickname, area, spinner_do, photoUrl);
            mDatabase.getReference().child("User_Info").child(cu).setValue(userInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            Toast.makeText(User_ProfileEditActivity.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Toast.makeText(User_ProfileEditActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                account_iv_profile.setImageBitmap(bitmap);
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
