package com.this25.safeteria.RegisterActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.this25.safeteria.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Menu_Add_Dialog extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private Context context;
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parant;
    private int pathCount, successCount;
    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();

    public Menu_Add_Dialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.activity_manager_menu_add);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText name = (EditText) dlg.findViewById(R.id.menu_name_edit);
        final EditText menual = (EditText) dlg.findViewById(R.id.menu_menual_edit);
        final EditText price = (EditText) dlg.findViewById(R.id.menu_price_edit);
        final Button button_dialog_submit = (Button) dlg.findViewById(R.id.button_dialog_submit);
        final Button button_dialog_imgadd = (Button) dlg.findViewById(R.id.button_dialog_imgadd);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        button_dialog_imgadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        button_dialog_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String store_name = name.getText().toString(); //메뉴이름
                final String store_menual = menual.getText().toString(); //메뉴 설명
                final String store_price = price.getText().toString(); //가격

                //final String date_tv = time;

                if (name.length() > 0 && menual.length() > 0 && price.length() > 0) {
                    final ArrayList<String> contentList = new ArrayList<>();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    if (pathList.size() != 0) {
                        final StorageReference mountainImageRef = storageRef.child("menu/" + user.getUid() + "/" + pathCount + ".jpg");
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentList.size() - 1)).build();
                            UploadTask uploadTask = mountainImageRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentList.set(index, uri.toString());
                                            successCount++;
                                            if (pathList.size() == successCount) {
                                                //완료
                                                Menu_Info menu_info = new Menu_Info(store_name, store_menual, store_price);
                                                storeUpload(menu_info);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러:" + e.toString());
                        }
                        pathCount++;
                        if (pathList.size() == 0) {
                            Menu_Info menu_info = new Menu_Info(store_name, store_menual, store_price);
                            storeUpload(menu_info);
                        }
                    } else {

                    }
                }
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
    private void storeUpload(Menu_Info menu_info) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("store").add(menu_info)
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
        Intent intent = new Intent(getApplicationContext(), c);
        context.startActivity(intent);
    }
}
