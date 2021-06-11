package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import amcoders.surfup.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText EmailField, PasswordField;
    private String email, password;
    private Button LoginButton;
    private RelativeLayout relativeLayout;
    private TextView forgotPasswordLink, SignupLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EmailField = (EditText) findViewById(R.id.login_email_field);
        PasswordField = (EditText) findViewById(R.id.login_password_field);
        LoginButton = (Button) findViewById(R.id.login_button);
        relativeLayout = (RelativeLayout) findViewById(R.id.login_relative_layout);
        forgotPasswordLink = (TextView) findViewById(R.id.login_forgot_password);
        SignupLink = (TextView) findViewById(R.id.login_signup_link);

        SignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailField.getText().toString();
                if (TextUtils.isEmpty(email))
                {
                    Snackbar snackbar = Snackbar.make(relativeLayout,
                            "We need to know your email address to reset your password", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Snackbar snackbar = Snackbar.make(relativeLayout,
                                                "Password reset mail sent successfully", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Snackbar snackbar = Snackbar.make(relativeLayout,
                                                message, Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                }
                            });
                }
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailField.getText().toString();
                password = PasswordField.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Snackbar snackbar = Snackbar.make(relativeLayout,
                            "Please fill all the credentials", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Snackbar snackbar = Snackbar.make(relativeLayout,
                                                "Login successful", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        Intent signupIntent = new Intent(LoginActivity.this, dashboardActivity.class);
                                        startActivity(signupIntent);
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Snackbar snackbar = Snackbar.make(relativeLayout,
                                                message, Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                }
                            });
                }
            }
        });

    }
}