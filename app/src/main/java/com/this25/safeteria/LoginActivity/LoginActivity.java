package com.this25.safeteria.LoginActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.RegisterActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private GoogleApiClient mGoogleApiClient;

    DatabaseReference mRoootRef = FirebaseDatabase.getInstance().getReference();
    // DatabaseReference 는 데이터베이스에서 데이터를 읽고 쓰려면 꼭 필요
    DatabaseReference conditionRdf = mRoootRef.child("text");

    //private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;

    Button google_sign_in_btn;
    Button facebook_sign_in_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        //getHashKey();

        mCallbackManager = CallbackManager.Factory.create();

        facebook_sign_in_btn = (Button) findViewById(R.id.btn);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        google_sign_in_btn = findViewById(R.id.google_login_btn);
        google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
            }
        }
    }

    //페이스북 로그인 메소드
    private void facebookLogin() {
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "Login"));
        //second step
        LoginManager.getInstance()
                .registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("Callback :: ", "onSuccess");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    // 로그인 창을 닫을 경우, 호출됩니다.
                    @Override
                    public void onCancel() {
                        Log.e("Callback :: ", "onCancel");
                    }
                    // 로그인 실패 시에 호출됩니다.
                    @Override
                    public void onError(FacebookException error) {
                        Log.e("Callback :: ", "onError : " + error.getMessage());
                    }
                });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Third step
                            // Sign in success, update UI with the signed-in user's information
                            gotomain();
                        } else {
                            // If sign in fails, display a message to the user.
                            toastMessage("페이스북 로그인에 실패하였습니다.");
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final RelativeLayout loderLayout = findViewById(R.id.loaderLayout);
        loderLayout.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loderLayout.setVisibility(View.GONE);
                        Log.v("알림", "ONCOMPLETE");
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.v("알림", "!task.isSuccessful()");
                        }else {
                            Log.v("알림", "task.isSuccessful()");
                            storageUpload("all");
                            gotomain();
                        }
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
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void gotomain() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}
