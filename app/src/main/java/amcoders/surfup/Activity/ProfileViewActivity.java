package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import amcoders.surfup.R;

public class ProfileViewActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView nickname;
    private Button logout;
    private FirebaseAuth mAuth;
    String currentUserID;
    private RelativeLayout relativeLayout;
    private DatabaseReference userref;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        nickname = findViewById(R.id.profile_nickname);
        email = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        userref.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nick_name = snapshot.child("NickName").getValue().toString();
                    nickname.setText(nick_name);
                }
                else{
                    nickname.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        bottomNavigationView = findViewById(R.id.profile_bottom_nav);
        logout = findViewById(R.id.logout_button);
        relativeLayout = findViewById(R.id.profile_view);




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Snackbar snackbar = Snackbar.make(relativeLayout,
                        "Successfully logged out!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

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