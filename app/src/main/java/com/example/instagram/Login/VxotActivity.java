package com.example.instagram.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.Activity.MainActivity;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VxotActivity extends AppCompatActivity {

    private LinearLayout bt_vxot;
    private EditText ed_password_vhot, ed_email_vxot;
    private FirebaseAuth auth;
    private TextView vxottext;
    private ProgressBar vxotProgresBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vxot);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        inite();
        userExamination();

        findViewById(R.id.liner_perexot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VxotActivity.this, RegistActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    // работа с кантенерами,отоброжения
    private void userExamination() {
        ed_password_vhot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                examination1(s);
            }

        });
    }

    // работа с кантенерами,отоброжения
    private void examination1(Editable s) {
        if (s.length() >= 6) {
            bt_vxot.setBackgroundResource(R.drawable.buuton_bacraund);
            vxottext.setTextColor(getResources().getColor(R.color.white));
            bt_vxot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vxotProgresBar.setVisibility(View.VISIBLE);
                    vxottext.setVisibility(View.GONE);
                    String emil = ed_email_vxot.getText().toString();
                    String password = ed_password_vhot.getText().toString();

                    if (TextUtils.isEmpty(emil) || TextUtils.isEmpty(password)) {
                    } else {
                        vxotUser(emil, password);
                    }
                }
            });
        } else {
            bt_vxot.setBackgroundResource(R.drawable.buuton_bacraundprozratni);
            vxottext.setTextColor(getResources().getColor(R.color.whiteprozrathi));
            vxotProgresBar.setVisibility(View.GONE);
            vxottext.setVisibility(View.VISIBLE);

        }
    }

    // метод для входа в аккаунт
    private void vxotUser(String emil, String password) {
        auth.signInWithEmailAndPassword(emil, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(VxotActivity.this, MainActivity.class));
                    finish();
                } else {
                    vxotProgresBar.setVisibility(View.GONE);
                    vxottext.setVisibility(View.VISIBLE);
                    Toast.makeText(VxotActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void inite() {
        ed_email_vxot = findViewById(R.id.ed_email_vxot);
        ed_password_vhot = findViewById(R.id.ed_password_vhot);
        bt_vxot = findViewById(R.id.bt_vxot);
        vxotProgresBar =findViewById(R.id.vxotProgresBar);
        vxottext = findViewById(R.id.vxottext);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(VxotActivity.this, MainActivity.class));
            finish();
        }
    }

}