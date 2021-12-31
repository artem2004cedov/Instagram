package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.Adapter.CatheAdapter;
import com.example.instagram.Model.MassegeModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView user_profile;
    private TextView username_that, text_send;
    private RecyclerView recycler_view_that;
    private ImageView image_cammerea, image_search;
    private CatheAdapter catheAdapter;
    private ArrayList<MassegeModel> massegeModels;
    private SocialAutoCompleteTextView chat_massage_edit;
    private LinearLayout liner_massage;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findViewById(R.id.image_that_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        user_profile = findViewById(R.id.user_profile);
        image_search = findViewById(R.id.image_search);
        image_cammerea = findViewById(R.id.image_cammerea);
        username_that = findViewById(R.id.username_that);
        chat_massage_edit = findViewById(R.id.chat_massage_edit);
        liner_massage = findViewById(R.id.liner_massage);
        text_send = findViewById(R.id.text_send);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        setMassage();

        String recieveid = getIntent().getStringExtra("userid");
        String profailPic = getIntent().getStringExtra("profailPic");
        String username = getIntent().getStringExtra("username");

        username_that.setText(username);
        if (profailPic.equals("default")) {
            user_profile.setImageResource(R.drawable.profilo);
        } else {
            Picasso.get().load(profailPic).into(user_profile);
        }

        recycler_view_that = findViewById(R.id.recycler_view_that);
        recycler_view_that.setHasFixedSize(true);
        recycler_view_that.setLayoutManager(new LinearLayoutManager(this));
        massegeModels = new ArrayList<>();
        catheAdapter = new CatheAdapter(massegeModels, this, recieveid);
        recycler_view_that.setAdapter(catheAdapter);


        final String senderid = auth.getUid();
        final String senderRoom = senderid + recieveid;
        final String receverRoom = recieveid + senderid;

        database.getReference().child("chats")
                .child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                massegeModels.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MassegeModel model = dataSnapshot.getValue(MassegeModel.class);
                    if (model != null) {
                        model.setMassageId(snapshot.getKey()); // под вопросом
                    }
                    massegeModels.add(model);
                }

                catheAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // добовления в базу
        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String massage = chat_massage_edit.getText().toString();
                // передается id зарегина пользовтеля и сообзение
                if (!TextUtils.isEmpty(chat_massage_edit.getText().toString())) {
                    final MassegeModel model = new MassegeModel(senderid, massage);
                    // передается время
                    model.setTimestamp(new Date().getTime());
                    chat_massage_edit.setText("");

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats")
                                    .child(receverRoom)
                                    .push() // разное
                                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                    });

                }
            }
        });


    }

    private void setMassage() {
        chat_massage_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getMassage(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
    }

    private void getMassage(Editable s) {
        if (s.length() == 0) {
            text_send.setVisibility(View.GONE);
            liner_massage.setVisibility(View.VISIBLE);
            image_cammerea.setVisibility(View.VISIBLE);
            image_search.setVisibility(View.GONE);
        } else {
            liner_massage.setVisibility(View.GONE);
            text_send.setVisibility(View.VISIBLE);
            image_cammerea.setVisibility(View.GONE);
            image_search.setVisibility(View.VISIBLE);
        }
    }
}