package com.this25.safeteria.HomeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.this25.safeteria.LoginActivity.LoginActivity;
import com.this25.safeteria.MapActivity.MapActivity;
import com.this25.safeteria.R;
import com.this25.safeteria.RegisterActivity.Manager_Info;
import com.this25.safeteria.RegisterActivity.RegisterActivity;
import com.this25.safeteria.SearchActivity.SearchActivity;
import com.this25.safeteria.SettingActivity.SettingActivity;
import com.this25.safeteria.WriteActivity.WriteActivity;
import com.this25.safeteria.WriteActivity.Write_data;
import com.this25.safeteria.adapter.ReviewAdpater;
import com.this25.safeteria.adapter.StoreAdapter;

import java.util.ArrayList;

public class HomeActivity extends Fragment {
    private View view;

    ImageButton setting_btn;
    Button search_bar_btn;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, null);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            myStartActivity(LoginActivity.class);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("TAG", "No such document");
                                myStartActivity(RegisterActivity.class);
                            }
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });


            db.collection("managers")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Manager_Info> postList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", document.getId() + " => " + document.getData());
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
                                }
                                RecyclerView recyclerView = view.findViewById(R.id.best_store);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
//                                    @Override
//                                    public boolean canScrollVertically() {
//                                        return false;
//                                    }
//                                };
//                                recyclerView.setLayoutManager(linearLayoutManager);
                                RecyclerView.Adapter mAdapter = new StoreAdapter(getActivity(), postList);
                                recyclerView.scrollToPosition(postList.size() - 1);
                                recyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
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
                                }
                                RecyclerView recyclerView = view.findViewById(R.id.best_review);
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
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        //환경설정
        setting_btn = view.findViewById(R.id.setting_button);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        //검색화면
        search_bar_btn = view.findViewById(R.id.search_bar_edit);
        search_bar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_main);
        fab.setOnClickListener(new HomeActivity.FABClickListener());

        return view;
    }
    class FABClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), WriteActivity.class);
            startActivity(intent);
        }
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }
}
