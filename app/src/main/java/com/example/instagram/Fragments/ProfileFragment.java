package com.example.instagram.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.PhotoAdapter;
import com.example.instagram.Adapter.ProfilAdapter;
import com.example.instagram.Adapter.UserRandomAdapterProfile;
import com.example.instagram.AddStorisActivity;
import com.example.instagram.ChatActivity;
import com.example.instagram.ChatUsersActivity;
import com.example.instagram.EditProfileActivity;
import com.example.instagram.FollowersActivity;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.Profil;
import com.example.instagram.Model.User;
import com.example.instagram.OptionsActivity;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPosts;

    private RecyclerView recycler_Profale;
    private ProfilAdapter profilAdapter;
    private List<Profil> profilList;

    private RecyclerView recyclerUserRandomProfile;
    private UserRandomAdapterProfile userRandomAdapterProfile;
    private List<User> userListRandomProfile;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;
    private boolean prof = false;
    private boolean biog = false;
    private boolean foll = false;

    private View view1;
    private View view2;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView followers, following;
    private TextView posts, count;
    private TextView fullname, text_all_random_profile;
    private TextView bio, textAddFoto, textAddStories;
    private TextView username;
    private ImageView myPictures,bottomArrow,doneIconImage;
    private ImageView savedPictures, imageprofileAdd;
    private Button editProfile, edit_following, edit_chats;
    private FirebaseUser fUser;
    private SharedPreferences sharedPref;
    private LinearLayout linearProfale, linearProfaleSave, layoutRandomProfile, layoutfill, linearprofileAdd, linearNoPublications;

    String profileId;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")) {
            // мой акаунт
            profileId = fUser.getUid();
        } else {
            // date кто пришел
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        sharedPref = this.getActivity().getSharedPreferences("Prof", Context.MODE_PRIVATE);
        init(view);

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
                recyclerView.setAdapter(photoAdapter);

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        bottomArrow = view.findViewById(R.id.bottomArrow);
        recyclerViewSaves = view.findViewById(R.id.recucler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();

        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        setrandomrecomendeitet(view);
        setProfale(view);
        getSavedPosts();
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();

        // artem
        if (profileId.equals("lnosYnOZz9MNEyo9Wmru4WheqzC2")) {
            doneIconImage.setVisibility(View.VISIBLE);
        } else {
            doneIconImage.setVisibility(View.GONE);
        }

        // если есть то
        if (profileId.equals(fUser.getUid())) {
            linearprofileAdd.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            edit_following.setVisibility(View.GONE);
            edit_chats.setVisibility(View.GONE);
            bottomArrow.setVisibility(View.VISIBLE);

            editProfile.setText("Редактировать профиль");
        } else {

            edit_following.setVisibility(View.VISIBLE);
            edit_chats.setVisibility(View.VISIBLE);
            linearprofileAdd.setVisibility(View.GONE);

            layoutfill.setVisibility(View.GONE);
            layoutRandomProfile.setVisibility(View.GONE);
            linearProfale.setVisibility(View.GONE);
            bottomArrow.setVisibility(View.GONE);
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();

                if (btnText.equals("Редактировать профиль")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
                    if (btnText.equals("follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                                .child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(fUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                                .child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(fUser.getUid()).removeValue();
                    }
                }
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackground(getResources().getDrawable(R.color.black));
                view2.setBackground(getResources().getDrawable(R.color.text_prost));
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackground(getResources().getDrawable(R.color.text_prost));
                view2.setBackground(getResources().getDrawable(R.color.black));
                linearProfale.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "Подписчики");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "Подписки");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void init(View view) {
        imageProfile = view.findViewById(R.id.image_profile);
        recyclerView = view.findViewById(R.id.recucler_view_pictures);
        linearNoPublications = view.findViewById(R.id.linearNoPublications);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        options = view.findViewById(R.id.options1);
        imageprofileAdd = view.findViewById(R.id.imageprofileAdd);
        linearprofileAdd = view.findViewById(R.id.linearprofileAdd);
        layoutfill = view.findViewById(R.id.layoutfill);
        count = view.findViewById(R.id.count);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        fullname = view.findViewById(R.id.fullname);
        linearProfale = view.findViewById(R.id.linearProfale);
        bio = view.findViewById(R.id.bio);
        doneIconImage = view.findViewById(R.id.doneIconImage);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        editProfile = view.findViewById(R.id.edit_profile);
        edit_following = view.findViewById(R.id.edit_following);
        edit_chats = view.findViewById(R.id.edit_chats);
        myPhotoList = new ArrayList<>();

        imageprofileAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutRandomProfile.getVisibility() == View.GONE) {
                    imageprofileAdd.setImageResource(R.drawable.addusericonfinal);
                    layoutRandomProfile.setVisibility(View.VISIBLE);
                } else {
                    imageprofileAdd.setImageResource(R.drawable.addusericon);
                    layoutRandomProfile.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setrandomrecomendeitet(View view) {
        Random random = new Random();
        List<User> userListRanFoll = new ArrayList<>();
        layoutRandomProfile = view.findViewById(R.id.layoutRandomProfile);
        text_all_random_profile = view.findViewById(R.id.text_all_random_profile);

        text_all_random_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id
                        .fragment_container, new InterestingPeopleFragment()).commit();
            }
        });

        recyclerUserRandomProfile = view.findViewById(R.id.recyclerUserRandomProfile);
        recyclerUserRandomProfile.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        userListRandomProfile = new ArrayList<>();

        userRandomAdapterProfile = new UserRandomAdapterProfile(getActivity(), userListRandomProfile, true);
        recyclerUserRandomProfile.setAdapter(userRandomAdapterProfile);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userListRandomProfile.clear();
                userListRanFoll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(fUser.getUid())) {
                        userListRanFoll.add(user);
                    }
                }

                Set<User> items_id = new HashSet<>();
                if (userListRanFoll.size() != 0) {
                    for (int i = 0; i < 30; i++) {
                        int index = random.nextInt(userListRanFoll.size());
                        User userRandom = userListRanFoll.get(index);
                        items_id.add(userRandom);
                    }
                }
                for (User user : items_id) {
                    userListRandomProfile.add(user);
                }
                userRandomAdapterProfile.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        textAddStories = view.findViewById(R.id.textAddStories);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderAl = new AlertDialog.Builder(getContext());
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.alerdialogprofile, null);
                builderAl.setView(layout);

                TextView textAddFoto = layout.findViewById(R.id.textAddFoto);
                textAddFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), EditProfileActivity.class));
                    }
                });

                TextView textAddStories = layout.findViewById(R.id.textAddStories);
                textAddStories.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), AddStorisActivity.class));
                    }
                });

                builderAl.show();

            }
        });

    }

    private void setProfale(View view) {
        recycler_Profale = view.findViewById(R.id.recycler_Profale);
        recycler_Profale.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        profilList = new ArrayList<>();
        profilAdapter = new ProfilAdapter(getContext(), profilList);

        String bio = sharedPref.getString("biop", "");
        int num = sharedPref.getInt("num", 0);

        if (bio.equals("")) {
            biog = false;
            profilList.add(new Profil(2, "iconbioset", "Добавьте биографию", "Раскажите своим", "подписичкам немного о себе.",
                    "Изменить биографию", "buuton_bacraund", "#FFFFFFFF"));
            profilAdapter.notifyDataSetChanged();
        } else {
            biog = true;
            profilList.add(new Profil(2, "bioprofalefinis", "Добавьте биографию",
                    "Раскажите своим", "подписичкам немного о себе.", "Изменить биографию", "item_prof", "#FF000000"));
            profilAdapter.notifyDataSetChanged();
        }

        if (num >= 1) {
            foll = true;
            profilList.add(new Profil(4, "profpipels", "Найдите, на кого\nподписаться",
                    "Подписитесь на людей и", "темы, которые вам интерестны", "Найти ещё", "item_prof", "#FF000000"));
            recycler_Profale.setAdapter(profilAdapter);
        } else {
            foll = false;
            profilList.add(new Profil(4, "proficicomstart", "Найдите, на кого\nподписаться",
                    "Подписитесь на людей и", "темы, которые вам интерестны", "Найти ещё", "buuton_bacraund", "#FFFFFFFF"));
            recycler_Profale.setAdapter(profilAdapter);
        }

        if (biog || prof || foll) {
            count.setText("3");
        }
        if (biog && prof && foll) {
            count.setText("4");
        }

        profilList.add(new Profil(1, "usernamefinal", "Добавьте свое имя", "Добавьте имя и фамилию,",
                "чтобы друзья узнали что эта вы", "Редактировать имя", "item_prof", "#FF000000"));
        profilAdapter.notifyDataSetChanged();

        recycler_Profale.setAdapter(profilAdapter);
    }

    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    savedIds.add(snapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        mySavedPosts.clear();

                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            Post post = snapshot1.getValue(Post.class);

                            for (String id : savedIds) {
                                if (post.getPostid().equals(id)) {
                                    mySavedPosts.add(post);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        myPhotoList.add(post);
                    }
                }
                if (myPhotoList.size() == 0) {
                    if (profileId.equals(fUser.getUid())) {
                        linearNoPublications.setVisibility(View.GONE);
                        linearProfale.setVisibility(View.VISIBLE);
                    } else {
                        linearNoPublications.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (profileId.equals(fUser.getUid())) {
                        linearProfale.setVisibility(View.GONE);
                    }
                    Collections.reverse(myPhotoList);
                    photoAdapter.notifyDataSetChanged();
                }
                if (myPhotoList.size() >= 3) {
                    if (profileId.equals(fUser.getUid())) {
                        imageprofileAdd.setImageResource(R.drawable.addusericon);
                        layoutRandomProfile.setVisibility(View.GONE);
                    }
                } else {
                    if (profileId.equals(fUser.getUid())) {
                        imageprofileAdd.setImageResource(R.drawable.addusericonfinal);
                        layoutRandomProfile.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatUsersActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()) {
                    edit_following.setVisibility(View.VISIBLE);
                    edit_chats.setVisibility(View.VISIBLE);
                    edit_following.setText("Отписаться");

                    edit_following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("Follow").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("following").child(profileId).setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("Follow").
                                    child(profileId).child("followers").child(FirebaseAuth.getInstance().getCurrentUser().
                                    getUid()).setValue(true);
                        }
                    });


                } else {
                    edit_following.setVisibility(View.VISIBLE);
                    edit_chats.setVisibility(View.VISIBLE);
                    edit_following.setText("Подписаться");

                    edit_following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("Follow").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("following").child(profileId).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Follow").
                                    child(profileId).child("followers").child(FirebaseAuth.getInstance().getCurrentUser().
                                    getUid()).removeValue();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostCount() {
        posts.setText(sharedPref.getString("postCounter", ""));

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)) counter++;
                }
                posts.setText(String.valueOf(counter));
                sharedPref.edit().putString("postCounter", String.valueOf(counter)).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowersAndFollowingCount() {

        followers.setText(sharedPref.getString("followers", ""));
        following.setText(sharedPref.getString("following", ""));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
                sharedPref.edit().putString("followers", "" + dataSnapshot.getChildrenCount()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharedPref.edit().putInt("num", (int) dataSnapshot.getChildrenCount()).apply();
                following.setText("" + dataSnapshot.getChildrenCount());
                sharedPref.edit().putString("following", "" + dataSnapshot.getChildrenCount()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void userInfo() {
        fullname.setText(sharedPref.getString("namep", ""));
        username.setText(sharedPref.getString("usernamep", ""));
        bio.setText(sharedPref.getString("biop", ""));

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                fullname.setText(user.getUsername());
                username.setText(user.getName());
                bio.setText(user.getBio());

                sharedPref.edit().putString("namep", user.getName()).apply();
                sharedPref.edit().putString("usernamep", user.getUsername()).apply();
                sharedPref.edit().putString("biop", user.getBio()).apply();

                if (user.getImageurl().equals("default")) {
                    prof = false;
                    imageProfile.setImageResource(R.drawable.profilo);
                    profilList.add(new Profil(3, "imageusericon", "Добавьте фото профиля", "Выберите фото для своего",
                            "профиля Instagram.", "Изменить фото", "buuton_bacraund", "#FFFFFFFF"));
                    profilAdapter.notifyDataSetChanged();
                } else {
                    prof = true;
                    profilList.add(new Profil(3, "proficonfinis", "Добавьте фото профиля",
                            "Выберите фото для своего", "профиля Instagram.", "Изменить фото", "item_prof", "#FF000000"));
                    profilAdapter.notifyDataSetChanged();
                    if (getView() != null) {
                        Glide.with(getView())
                                .load(user.getImageurl())
                                .into(imageProfile);
                    }
                }

                if (biog || prof || foll) {
                    count.setText("3");
                }
                if (biog && prof && foll) {
                    count.setText("4");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
