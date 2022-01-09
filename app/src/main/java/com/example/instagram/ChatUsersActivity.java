package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.ChatUserAdapter;
import com.example.instagram.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersActivity extends AppCompatActivity {
    private RecyclerView recycler_chat;
    private ChatUserAdapter chatAdapter;
    private SocialAutoCompleteTextView search_user;
    private List<User> userList;
    private FirebaseUser fUser;
    private TextView name_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chath_user);

        name_chat = findViewById(R.id.name_chat);
        recycler_chat = findViewById(R.id.recycler_chat);
        search_user = findViewById(R.id.search_user);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recycler_chat.setHasFixedSize(true);
        recycler_chat.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        chatAdapter = new ChatUserAdapter(this, userList);
        recycler_chat.setAdapter(chatAdapter);

        getUser();
        getUsername();

        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
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