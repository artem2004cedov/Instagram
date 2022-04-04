package com.example.instagram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.example.instagram.Adapter.ChatPepleOnlaneAdapter;
import com.example.instagram.Adapter.ChatUserAdapter;
import com.example.instagram.Adapter.StorisAdapter;
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatUsersActivity extends AppCompatActivity {
    private RecyclerView recycler_chat;
    private ChatUserAdapter chatAdapter;
    private SocialAutoCompleteTextView search_user;
    private List<User> userList;
    private FirebaseUser fUser;
    private TextView name_chat;

    private RecyclerView recycler_view_online;
    private List<User> userListOnline;
    private ChatPepleOnlaneAdapter chatPepleOnlaneAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chath_user);

        name_chat = findViewById(R.id.name_chat);
        recycler_view_online = findViewById(R.id.recycler_view_online);
        recycler_chat = findViewById(R.id.recycler_chat);
        search_user = findViewById(R.id.search_user);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recycler_chat.setHasFixedSize(true);
        recycler_chat.setLayoutManager(new LinearLayoutManager(this));

        userListOnline = new ArrayList<>();
        recycler_view_online.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recycler_view_online.setHasFixedSize(true);
        chatPepleOnlaneAdapter = new ChatPepleOnlaneAdapter(this, userListOnline);
        recycler_view_online.setAdapter(chatPepleOnlaneAdapter);

        userList = new ArrayList<>();
        chatAdapter = new ChatUserAdapter(this, userList);
        recycler_chat.setAdapter(chatAdapter);

        Context context = recycler_chat.getContext();
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.loyout_slide_recycler);
        recycler_chat.setLayoutAnimation(layoutAnimationController);
        recycler_chat.getAdapter().notifyDataSetChanged();
        recycler_chat.scheduleLayoutAnimation();


        getUser();
        getUsername();
        readUsersOnline();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>();
                map.put("status", "Сейчас в сети");
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
            }
        }, 1000);


        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("status", "Сейчас в сети");
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);

                startActivity(new Intent(ChatUsersActivity.this, MainActivity.class));
            }
        });
    }

    private void readUsersOnline() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userListOnline.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        if (user.getStatus().equals("Сейчас в сети")) {

                            userListOnline.add(user);

                        }
                    }
                }

                if (userListOnline.size() == 0) {
                    recycler_view_online.setVisibility(View.GONE);
                } else {
                    recycler_view_online.setVisibility(View.VISIBLE);
                }

                chatPepleOnlaneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setUseridraidom(dataSnapshot.getKey());
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        userList.add(user);
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Map<String, Object> map = new HashMap<>();
        map.put("status", "Был недавно ");
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.online();
    }

    private void searchUser(String s) {

        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid()))
                        userList.add(user);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.onlineTame();
    }

    private void getUsername() {
        name_chat.setText(getSharedPreferences("thatUser", MODE_PRIVATE).getString("nameChat", ""));
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name_chat.setText(user.getUsername());
                getSharedPreferences("thatUser", MODE_PRIVATE).edit().putString("nameChat", user.getUsername()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}