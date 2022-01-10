package com.example.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.PostAdapter;
import com.example.instagram.Adapter.StorisAdapter;
import com.example.instagram.Adapter.UserRandomAdapter;
import com.example.instagram.AddStorisActivity;
import com.example.instagram.ChatUsersActivity;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private RecyclerView recycler_view_story;
    private RecyclerView recyclerUserRandom;
    private RecyclerView recyclerLower;
    private PostAdapter postAdapter;
    private StorisAdapter storisAdapter;
    private List<Post> postList;
    private List<Post> postListLower;
    private List<Stories> storiesList;
    private CircleImageView image_storis;
    private FirebaseUser fUser;
    private LinearLayout layoutRandom;

    private List<User> listRandom;
    private UserRandomAdapter userRandomAdapter;

    private Random randomGenerator;
    private List<User> follListRandom;
    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postListLower = new ArrayList<>();
        randomGenerator = new Random();
        layoutRandom = view.findViewById(R.id.layoutRandom);


        listRandom = new ArrayList<>();
        follListRandom = new ArrayList<>();
        recyclerUserRandom = view.findViewById(R.id.recyclerUserRandom);
        recyclerUserRandom.setHasFixedSize(true);
        recyclerUserRandom.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        userRandomAdapter = new UserRandomAdapter(getContext(), listRandom, true);
        recyclerUserRandom.setAdapter(userRandomAdapter);
        readUsers();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        image_storis = view.findViewById(R.id.image_storis);

        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setStackFromEnd(true);
        linearLayoutManager1.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager1);
        postList = new ArrayList<>();

        recyclerLower = view.findViewById(R.id.recyclerLower);
        recyclerLower.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler_view_story = view.findViewById(R.id.recycler_view_story);
        recycler_view_story.setHasFixedSize(true);
        recycler_view_story.setLayoutManager(new GridLayoutManager(getActivity(), 1, LinearLayoutManager.HORIZONTAL,
                false));
        storiesList = new ArrayList<>();
        storisAdapter = new StorisAdapter(getContext(), storiesList);
        recycler_view_story.setAdapter(storisAdapter);
        followingList = new ArrayList<>();

        checkFollowingUsers();

        view.findViewById(R.id.startThat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChatUsersActivity.class));
            }
        });

        getImageUser();

        image_storis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddStorisActivity.class));
            }
        });

        view.findViewById(R.id.text_all_random).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new InterestingPeopleFragment()).commit();
            }
        });

        return view;
    }

    // если ты потписан то показывай посты других
    private void readPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList) {
                        if (post.getPublisher().equals(id)) {
                            postList.add(post);
                        }
                    }
                }

                if (postList.size() != 0) {
                    List<Post> posL = new ArrayList<>();
                    Post post = postList.get(0);
                    if (postList.size() >= 2) {
                        Post post1 = postList.get(1);
                        posL.add(post1);
                    }
                    posL.add(post);
                    postAdapter = new PostAdapter(getContext(), posL);
                    recyclerViewPosts.setAdapter(postAdapter);
                    postAdapter.notifyDataSetChanged();
                }

                if (postList.size() >= 2) {
                    List<Post> pList = new ArrayList<>();
                    pList.addAll(postList);
                    pList.remove(0);
                    pList.remove(0);
                    postAdapter = new PostAdapter(getContext(), pList);
                    recyclerLower.setAdapter(postAdapter);
                    postAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRandom.clear();
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(fUser.getUid())) {
                        follListRandom.add(user);

                    }
                }

                Set<User> items_id = new HashSet<>();
                if (follListRandom.size() != 0) {
                    layoutRandom.setVisibility(View.VISIBLE);

                    if (follListRandom.size() != 0) {
                        for (int i = 0; i < 16; i++) {
                            int index = randomGenerator.nextInt(follListRandom.size());
                            User random = follListRandom.get(index);
                            items_id.add(random);
                        }
                    }
                }
                for (User user : items_id) {
                    listRandom.add(user);
                }

                userRandomAdapter.notifyDataSetChanged();
            }


        @Override
        public void onCancelled (@NonNull DatabaseError databaseError){

        }
    });
}

    private void getImageUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    image_storis.setImageResource(R.drawable.profilo);
                } else {
                    Glide.with(getContext())
                            .load(user.getImageurl())
                            .into(image_storis);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followingList.add(snapshot.getKey());
                }
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readPosts();
                readStories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readStories() {
        FirebaseDatabase.getInstance().getReference().child("Stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storiesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Stories stories = snapshot.getValue(Stories.class);
                    for (String id : followingList) {
                        if (stories.getPublisher().equals(id)) {
                            storiesList.add(stories);
                        }
                    }
                }
                storisAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
