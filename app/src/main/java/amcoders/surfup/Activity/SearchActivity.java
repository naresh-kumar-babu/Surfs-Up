package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import amcoders.surfup.R;

public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView = findViewById(R.id.search_bottom_nav);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_search:
                        return true;
                    case R.id.menu_home:
                        Intent pIntent = new Intent(SearchActivity.this, dashboardActivity.class);
                        startActivity(pIntent);
                        return true;
                    case R.id.menu_profile:
                        Intent sIntent = new Intent(SearchActivity.this, ProfileViewActivity.class);
                        startActivity(sIntent);
                        return true;
                }
                return false;
            }
        });
    }
}