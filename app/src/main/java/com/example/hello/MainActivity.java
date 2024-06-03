package com.example.hello;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchBtn;
    ChatFragment ChatFragment;
    ProfileFragment ProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //assign id's for variables
        ChatFragment = new ChatFragment();
        ProfileFragment = new ProfileFragment();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        searchBtn = findViewById(R.id.mainSearchBtn);

        //search button
        searchBtn.setOnClickListener((v)-> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        //bottom navigation bar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menuChat)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,ChatFragment).commit();
                }
                if(item.getItemId()==R.id.menuProfile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,ProfileFragment).commit();
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menuChat);
    }
}