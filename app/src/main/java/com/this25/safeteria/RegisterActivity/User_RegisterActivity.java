package com.this25.safeteria.RegisterActivity;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.this25.safeteria.MainActivity.MainActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.WriteActivity.Write_data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User_RegisterActivity extends AppCompatActivity {
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
    private ArrayList<String> pathList = new ArrayList<>();
    private int pathCount, successCount;

    ArrayAdapter<CharSequence> adspin1, adspin2; //어댑터를 선언했습니다. adspint1(서울,인천..) adspin2(강남구,강서구..)
    String choice_do="";
    String choice_se="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
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
                    adspin2 = ArrayAdapter.createFromResource(User_RegisterActivity.this, R.array.spinner_do_seoul, R.layout.row_spinner);
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
                    adspin2 = ArrayAdapter.createFromResource(User_RegisterActivity.this, R.array.spinner_do_incheon, R.layout.row_spinner);
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
                    adspin2 = ArrayAdapter.createFromResource(User_RegisterActivity.this, R.array.spinner_do_gwangju,R.layout.row_spinner);
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
                    adspin2 = ArrayAdapter.createFromResource(User_RegisterActivity.this, R.array.spinner_do_daegu, R.layout.row_spinner);
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
                    adspin2 = ArrayAdapter.createFromResource(User_RegisterActivity.this, R.array.spinner_do_ulsan, R.layout.row_spinner);
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
                    adspin2 = ArrayAdapter.createFromResource(User_RegisterActivity.this, R.array.spinner_do_daejeon, R.layout.row_spinner);
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
                    ok.setBackgroundResource(R.drawable.dhksfy_on);
                    check.setImageResource(R.drawable.okok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            storageUpload(nickname_edit.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if(text.length() < 4) {
                    check.setImageResource(R.drawable.back_white);
                    check_tv.setText("4글자 이상을 입력해 주세요.");
                    check_tv.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력이 끝났을 때
                String text = s.toString();
                if(text.length() >= 4) {
                    check_tv.setText("사용할 수 있는 닉네임입니다.");
                    ok.setBackgroundResource(R.drawable.dhksfy_on);
                    check.setImageResource(R.drawable.okok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            storageUpload(nickname_edit.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
    private void storageUpload(final String nickname) {
        FirebaseUser user = mAuth.getCurrentUser();
        final String cu = mAuth.getUid();
        final String area = spin1.getSelectedItem().toString();
        final String spinner_do = spin2.getSelectedItem().toString();
        final String name = user.getDisplayName();
        final String email = user.getEmail();
        //String photoUrl = filename;
        Log.v("알림", "현재로그인한 유저 " + cu);
        Log.v("알림", "현재로그인한 이메일 " + email);
        Log.v("알림", "현재로그인한 유저 닉네임 " + nickname);
        Log.v("알림", "유저 이름 " + name);
        Log.v("알림", "유저 지역 " + area);
        Log.v("알림", "유저 상세지역 " + spinner_do);

        final RelativeLayout loderLayout = findViewById(R.id.loaderLayout);
        loderLayout.setVisibility(View.VISIBLE);

        //final String date_tv = time;

        if(nickname.length() > 0 && filePath != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now);
            final ArrayList<String> contentList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://safeteria-1ffcb.appspot.com").child("User").child(cu).child("images/" + "profile.png");
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
                loderLayout.setVisibility(View.GONE);
                User_Info userInfo = new User_Info(name, cu, email, nickname, area, spinner_do, "photoUrl");
                storeUpload(userInfo);
            } else {

            }
        }
    }
    private void storeUpload(User_Info user_info) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db.collection("/users").document(user.getUid()).set(user_info)
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
}
