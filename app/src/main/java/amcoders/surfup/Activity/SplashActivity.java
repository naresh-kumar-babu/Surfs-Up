package amcoders.surfup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import amcoders.surfup.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=4000;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if (mAuth.getCurrentUser() == null)
                {
                    i=new Intent(SplashActivity.this,
                            LoginActivity.class);
                }
                else {
                    i = new Intent(SplashActivity.this,
                            MainActivity.class);
                }

                startActivity(i);

                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

}