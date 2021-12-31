package com.example.instagram.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.MainActivity;
import com.example.instagram.PhotoinprofileActivity;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistActivity extends AppCompatActivity {

    private EditText ed_name, ed_email, ed_password, ed_username;
    private Button bt_registr, bt_next_, bt_next_registr, bt_next_name;
    private DatabaseReference mRootRef;
    private TextView text_email, changeName_;
    private LinearLayout layout_email, layout_registr, layout_finis, layout_name;
    private FirebaseAuth auth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        inite();
        containers();

        bt_registr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ed_email.getText().toString();
                String name = ed_name.getText().toString();
                String username = ed_username.getText().toString();
                String password = ed_password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {

                } else {
                    registrUser(email, name, password, username);
                }
            }
        });
    }

    private void containers() {
        ed_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                continu(s);
            }
        });

        ed_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                next(s);
            }
        });


        bt_next_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_finis.setVisibility(View.VISIBLE);
                layout_name.setVisibility(View.GONE);
            }
        });
    }

    private void continu(Editable s) {
        if (s.length() >= 6) {
            bt_next_registr.setBackgroundResource(R.drawable.buuton_bacraund);
            bt_next_registr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_email.setText(ed_email.getText().toString() + "?");
                    layout_registr.setVisibility(View.GONE);
                    layout_finis.setVisibility(View.VISIBLE);

                    changeName_.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_finis.setVisibility(View.GONE);
                            layout_name.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else {
            bt_next_registr.setBackgroundResource(R.drawable.buuton_bacraundprozratni);
        }
    }


    private void next(Editable s) {
        if (s.length() == 0) {
            bt_next_.setBackgroundResource(R.drawable.buuton_bacraundprozratni);
        } else {
            bt_next_.setBackgroundResource(R.drawable.buuton_bacraund);
            if (s.length() >= 6)
                bt_next_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout_email.setVisibility(View.GONE);
                        layout_registr.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    private void registrUser(String email, String name, String password, String username) {
        pd.setTitle("Регистрация");
        pd.setMessage("Пожалуйста подождите...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("username", username);
                map.put("id", auth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageurl", "default");

                mRootRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(RegistActivity.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistActivity.this, PhotoinprofileActivity.class));
                            finish();
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegistActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void inite() {
        pd = new ProgressDialog(this);
        ed_email = findViewById(R.id.ed_email);
        ed_username = findViewById(R.id.ed_username);
        bt_next_ = findViewById(R.id.bt_next_);
        bt_registr = findViewById(R.id.bt_registr);
        text_email = findViewById(R.id.text_email);
        bt_next_registr = findViewById(R.id.bt_next_registr);
        layout_email = findViewById(R.id.layout_email);
        layout_registr = findViewById(R.id.layout_registr);
        layout_name = findViewById(R.id.layout_name);
        changeName_ = findViewById(R.id.changeName_);
        bt_next_name = findViewById(R.id.bt_next_name);
        layout_finis = findViewById(R.id.layout_finis);
        ed_name = findViewById(R.id.ed_name);
        ed_password = findViewById(R.id.ed_password);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        findViewById(R.id.liner_perexot_nazad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}