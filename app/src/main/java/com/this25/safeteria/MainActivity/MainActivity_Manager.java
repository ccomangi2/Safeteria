package com.this25.safeteria.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.this25.safeteria.HomeActivity.HomeActivity;
import com.this25.safeteria.MapActivity.MapActivity;
import com.this25.safeteria.ProfileActivity.Manager_ProfileActivity;
import com.this25.safeteria.ProfileActivity.ProfileActivity;
import com.this25.safeteria.R;

public class MainActivity_Manager extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeActivity fragmentHome = new HomeActivity();
    private MapActivity fragmentMap = new MapActivity();
    private Manager_ProfileActivity fragmentProfile = new Manager_ProfileActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.homeItem:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.mapItem:
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                    break;
                case R.id.profileItem:
                    transaction.replace(R.id.frameLayout, fragmentProfile).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
