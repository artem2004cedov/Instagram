package com.example.instagram.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.example.instagram.SplashScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.HashMap;

public class VxotActivity extends AppCompatActivity {

    private Button bt_vxot;
    private EditText ed_password_vhot, ed_email_vxot;
    private FirebaseAuth auth;
    private boolean isLast1;
    private boolean isLas2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vxot);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        inite();
        userExamination();

        findViewById(R.id.liner_perexot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VxotActivity.this, RegistActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        bt_vxot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emil = ed_email_vxot.getText().toString();
                String password = ed_password_vhot.getText().toString();

                if (TextUtils.isEmpty(emil) || TextUtils.isEmpty(password)) {
                } else {
                    vxotUser(emil, password);
                }
            }
        });

    }

    private void userExamination() {
        ed_email_vxot.addTextChangedListener(new TextWatcher() {
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


        ed_password_vhot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                examination2(s);
            }
        });
    }


    private void examination1(Editable s) {
        if (s.length() == 0) {
            bt_vxot.setBackgroundResource(R.drawable.buuton_bacraundprozratni);
            isLast1 = false;
        } else {
//            checkingVariables();
            isLast1 = true;
            bt_vxot.setBackgroundResource(R.drawable.buuton_bacraund);
        }
    }

    private void examination2(Editable s) {

    }

    private void vxotUser(String emil, String password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", emil);
        map.put("password", password);
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Email | Password").setValue(map);
        auth.signInWithEmailAndPassword(emil, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VxotActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VxotActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(VxotActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void inite() {
        ed_email_vxot = findViewById(R.id.ed_email_vxot);
        ed_password_vhot = findViewById(R.id.ed_password_vhot);
        bt_vxot = findViewById(R.id.bt_vxot);
        auth = FirebaseAuth.getInstance();
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