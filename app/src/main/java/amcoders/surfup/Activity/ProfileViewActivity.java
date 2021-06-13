package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import amcoders.surfup.R;

public class ProfileViewActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        bottomNavigationView = findViewById(R.id.profile_bottom_nav);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_search:
                        Intent sIntent = new Intent(ProfileViewActivity.this, SearchActivity.class);
                        startActivity(sIntent);
                        return true;
                    case R.id.menu_home:
                        Intent pIntent = new Intent(ProfileViewActivity.this, dashboardActivity.class);
                        startActivity(pIntent);
                        return true;
                    case R.id.menu_profile:
                        return true;
                }
                return false;
            }
        });
    }
}