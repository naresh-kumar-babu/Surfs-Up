package amcoders.surfup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import amcoders.surfup.R;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText FullnameET, NicknameET;
    private Button continuebtn;
    private TextView MaleT, FemaleT, OtherT, DateT;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    boolean maleb,femaleb, otherb;
    String fullname,nickname,gender,date;
    String country = "";
    int year,month,day;
    private String uid;
    private RelativeLayout relativeLayout;
    private DatabaseReference usersRef;
    Spinner countryspin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        relativeLayout = (RelativeLayout) findViewById(R.id.profile_relative_layout);
        MaleT = findViewById(R.id.male_select);
        FemaleT = findViewById(R.id.female_select);
        OtherT = findViewById(R.id.other_select);
        DateT = findViewById(R.id.dob);
        countryspin = findViewById(R.id.country);

        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        countries.add(0,"Country");
        countryspin.setAdapter(new ArrayAdapter<>(ProfileActivity.this,
                android.R.layout.simple_spinner_dropdown_item,countries));
        countryspin.setSelection(0, false);




        DateT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProfileActivity.this,
                        android.R.style.Theme_Material_Light_Dialog,
                        mDateSetListener,
                        day,month,year
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1+1;
                day = i2;
                date = day + "/" + month + "/" + year;
                DateT.setText(date);
            }
        };

        MaleT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleT.setBackgroundColor(getResources().getColor(R.color.blue));
                MaleT.setTextColor(getResources().getColor(R.color.white));
                OtherT.setTextColor(getResources().getColor(R.color.black));
                FemaleT.setTextColor(getResources().getColor(R.color.black));
                FemaleT.setBackground(Drawable.createFromPath("@drawable/rounder_field"));
                OtherT.setBackground(Drawable.createFromPath("@drawable/rounder_field"));
                maleb = true;
                femaleb = false;
                otherb = false;
            }
        });
        FemaleT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleT.setBackground(Drawable.createFromPath("@drawable/rounder_field"));
                FemaleT.setBackgroundColor(getResources().getColor(R.color.blue));
                FemaleT.setTextColor(getResources().getColor(R.color.white));
                MaleT.setTextColor(getResources().getColor(R.color.black));
                OtherT.setTextColor(getResources().getColor(R.color.black));
                OtherT.setBackground(Drawable.createFromPath("@drawable/rounder_field"));
                maleb = false;
                femaleb = true;
                otherb = false;

            }
        });
        OtherT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleT.setBackground(Drawable.createFromPath("@drawable/rounder_field"));
                FemaleT.setBackground(Drawable.createFromPath("@drawable/rounder_field"));
                OtherT.setTextColor(getResources().getColor(R.color.white));
                MaleT.setTextColor(getResources().getColor(R.color.black));
                FemaleT.setTextColor(getResources().getColor(R.color.black));
                OtherT.setBackgroundColor(getResources().getColor(R.color.blue));
                maleb = false;
                femaleb = false;
                otherb = true;

            }
        });

        NicknameET = findViewById(R.id.nickname);
        FullnameET = findViewById(R.id.fullname);
        continuebtn = findViewById(R.id.continue_button);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maleb || femaleb || otherb)
                {
                    if (maleb)
                        gender = "Male";
                    if(femaleb)
                        gender = "Female";
                    if(otherb)
                        gender = "Other";
                }
                fullname = FullnameET.getText().toString();
                nickname = NicknameET.getText().toString();
                if(TextUtils.isEmpty(country))
                {
                    Snackbar snackbar = Snackbar.make(relativeLayout,
                            "Please Select Your Country", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                countryspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0){
                            country = "";
                            Snackbar snackbar = Snackbar.make(relativeLayout,
                                    "Please Select Your Country", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }else{
                            String scountry = parent.getItemAtPosition(position).toString();
                            country = scountry;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(date) || TextUtils.isEmpty(gender)  || TextUtils.isEmpty(nickname))
                {
                    Snackbar snackbar = Snackbar.make(relativeLayout,
                            "Please fill the details. ", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                if(!(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(date) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(nickname) || TextUtils.isEmpty(country)))
                {
                    usersRef = FirebaseDatabase.getInstance().getReference("Users");
                    uid = mAuth.getCurrentUser().getUid();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put("FullName",fullname);
                    result.put("DOB",date);
                    result.put("Gender",gender);
                    result.put("NickName",nickname);
                    result.put("Country",country);
                    usersRef.child(uid).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Intent mainIntent = new Intent(ProfileActivity.this, dashboardActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Snackbar snackbar = Snackbar.make(relativeLayout,
                                        "Error occurred. "+message, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    });
                     /*
                    Snackbar snackbar = Snackbar.make(relativeLayout,fullname+", "+nickname+", "+date+", "+gender+", "+country, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Intent mainIntent = new Intent(ProfileActivity.this, dashboardActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                      */
                }
            }
        });
    }
}