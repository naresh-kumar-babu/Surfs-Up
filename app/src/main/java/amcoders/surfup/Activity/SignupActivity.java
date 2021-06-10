package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import amcoders.surfup.R;

public class SignupActivity extends AppCompatActivity {
    private RelativeLayout rlayout;
    private Animation animation;
    private EditText EmailET, PasswordET, RePasswordET;
    private Button Signupbutton;
    private FirebaseAuth mAuth;
    String email, password, re_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EmailET = findViewById(R.id.signup_email_field);
        PasswordET = findViewById(R.id.signup_password_field);
        RePasswordET = findViewById(R.id.signup_confirm_password_field);
        rlayout = findViewById(R.id.signup_relative_layout);
        Signupbutton = findViewById(R.id.signup_button);

        final ProgressBar progressBar = findViewById(R.id.spin_kit_reg);
        Sprite doubleBounce = new FoldingCube();
        progressBar.setIndeterminateDrawable(doubleBounce);

        mAuth = FirebaseAuth.getInstance();

        Signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailET.getText().toString();
                password = PasswordET.getText().toString();
                re_password = RePasswordET.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(re_password))
                {
                    Snackbar snackbar = Snackbar.make(rlayout,
                            "All the fields are mandatory", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                }
                if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(re_password)))
                {
                    if(password.equals(re_password))
                    {
                        mAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @SuppressLint("UseCompatLoadingForColorStateLists")
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful())
                                        {
                                            Signupbutton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.green));
                                            progressBar.setVisibility(View.VISIBLE);
                                            Intent mainIntent = new Intent(SignupActivity.this, ProfileActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Snackbar snackbar = Snackbar.make(rlayout,
                                                    "Error occurred. "+message, Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }

                                    }
                                });
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(rlayout,
                                "Passwords do not match", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}