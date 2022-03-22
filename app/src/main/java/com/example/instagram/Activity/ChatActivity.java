package com.example.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.CatheAdapter;
import com.example.instagram.Model.MassegeModel;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView user_profile,thatUserProfile,thatDot;
    private TextView username_that, text_send,statusTextThat,thatUserName,thatName,thatPostCount,thatfollowers,thatUserFollowing;
    private RecyclerView recycler_view_that;
    private ImageView image_cammerea, image_search;
    private CatheAdapter catheAdapter;
    private ArrayList<MassegeModel> massegeModels;
    private SocialAutoCompleteTextView chat_massage_edit;
    private LinearLayout liner_massage,ThatLinerL;
    private FirebaseDatabase database;
    private Button visitProfileBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViewById(R.id.image_that_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("status", "Сейчас в сети");
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
                startActivity(new Intent(getApplicationContext(),ChatUsersActivity.class));
            }
        });

        init();
        setMassage();

        String recieveid = getIntent().getStringExtra("useridrandom");
        String profailPic = getIntent().getStringExtra("profailPic");
        String username = getIntent().getStringExtra("username");
        String name = getIntent().getStringExtra("name");
        String userstatus = getIntent().getStringExtra("userstatus");
        String userId = getIntent().getStringExtra("userid");

        username_that.setText(name);

        if (userstatus.equals("Сейчас в сети")) {
            thatDot.setVisibility(View.VISIBLE);
        } else {
            thatDot.setVisibility(View.GONE);
        }
        statusTextThat.setText(userstatus);
        thatName.setText(name);
        thatUserName.setText(" " + username);

        if (profailPic.equals("default")) {
            user_profile.setImageResource(R.drawable.profilo);
            thatUserProfile.setImageResource(R.drawable.profilo);
        } else {
            Picasso.get().load(profailPic).into(user_profile);
            Picasso.get().load(profailPic).into(thatUserProfile);
        }

        recycler_view_that = findViewById(R.id.recycler_view_that);
        recycler_view_that.setHasFixedSize(true);
        recycler_view_that.setLayoutManager(new LinearLayoutManager(this));
        massegeModels = new ArrayList<>();
        catheAdapter = new CatheAdapter(massegeModels, this, recieveid);
        recycler_view_that.setAdapter(catheAdapter);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>();
                map.put("status", "Сейчас в сети");
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
            }
        }, 1000);


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

                int num = (int) snapshot.getChildrenCount();

                if (num >= 1) {
                    ThatLinerL.setVisibility(View.GONE);
                    recycler_view_that.setVisibility(View.VISIBLE);
                } else {
                    ThatLinerL.setVisibility(View.VISIBLE);
                    recycler_view_that.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // количисто постов
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(userId)) counter++;
                }
                thatPostCount.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//      Количетво Попписчиков
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thatfollowers.setText("" + dataSnapshot.getChildrenCount() + " ");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // переход в

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").
                child(FirebaseAuth.getInstance().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists())
                    thatUserFollowing.setText("Вы подписаны на друг друга в Instagram");
                else
                    thatUserFollowing.setText("Вы не подписаны на друг друга в Instagram");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        visitProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("publisherId",userId));
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

    private void init() {
        user_profile = findViewById(R.id.user_profile);
        thatDot = findViewById(R.id.thatDot);
        thatfollowers = findViewById(R.id.thatfollowers);
        thatPostCount = findViewById(R.id.thatPostCount);
        visitProfileBtn = findViewById(R.id.visitProfileBtn);
        ThatLinerL = findViewById(R.id.ThatLinerL);
        thatUserProfile = findViewById(R.id.thatUserProfile);
        statusTextThat = findViewById(R.id.statusTextThat);
        image_search = findViewById(R.id.image_search);
        image_cammerea = findViewById(R.id.image_cammerea);
        thatUserFollowing = findViewById(R.id.thatUserFollowing);
        username_that = findViewById(R.id.username_that);
        chat_massage_edit = findViewById(R.id.chat_massage_edit);
        liner_massage = findViewById(R.id.liner_massage);
        text_send = findViewById(R.id.text_send);
        thatUserName = findViewById(R.id.thatUserName);
        thatName = findViewById(R.id.thatName);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.onlineTame();
    }

    @Override
    protected void onStop() {
        super.onStop();
       MainActivity.offline();
    }

    @Override
    protected void onStart() {
        super.onStart();
       MainActivity.online();
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