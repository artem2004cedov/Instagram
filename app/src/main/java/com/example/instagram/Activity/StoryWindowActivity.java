package com.example.instagram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Model.Profil;
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryWindowActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private ImageView mainWindowStories, add_comment_stories, settingStory, profileStory, settingsStoryBottom;
    private CircleImageView stories_profale;
    private TextView name_user_stories, story_con;
    private TextInputEditText massege_stories;

    private LinearLayout linearStory, linearStoryMy, linearView;

    private int count = 0;
    private long progressTime = 0L;
    long limit = 500L;
    private StoriesProgressView progress;
    private int contView;

    private List<String> images;
    private List<String> storyId;
    String userId;

    private View reverse, skip;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    progressTime = System.currentTimeMillis();
                    progress.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = progressTime = System.currentTimeMillis();
                    progress.resume();
                    return limit < now - progressTime;

            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_window);

        /*При переходе после регистрации сделать какойта ключь */

        mainWindowStories = findViewById(R.id.mainWindowStories);
        stories_profale = findViewById(R.id.stories_profale);
        name_user_stories = findViewById(R.id.name_user_stories);
        massege_stories = findViewById(R.id.massege_stories);
        add_comment_stories = findViewById(R.id.add_comment_stories);
        profileStory = findViewById(R.id.profileStory);

        reverse = findViewById(R.id.reverse);
        settingsStoryBottom = findViewById(R.id.settingsStoryBottom);
        linearStoryMy = findViewById(R.id.linearStoryMy);
        linearStory = findViewById(R.id.linearStory);
        linearView = findViewById(R.id.linearView);
        skip = findViewById(R.id.skip);
        progress = findViewById(R.id.progress);
        settingStory = findViewById(R.id.settingStory);
        userId = getIntent().getStringExtra("userId");

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            linearStory.setVisibility(View.GONE);
            linearStoryMy.setVisibility(View.VISIBLE);
            settingsStoryBottom.setVisibility(View.VISIBLE);
            settingStory.setVisibility(View.GONE);
        } else {
            linearStory.setVisibility(View.VISIBLE);
            settingStory.setVisibility(View.VISIBLE);
            linearStoryMy.setVisibility(View.GONE);
            settingsStoryBottom.setVisibility(View.GONE);
        }

        getUser();

        settingStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StoryWindowActivity.this);
                    bottomSheetDialog.setContentView(R.layout.setting_story_my);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    progress.pause();

                    TextView deleteStory = bottomSheetDialog.findViewById(R.id.deleteStory);
                    deleteStory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                                    child("Story").child(userId).child(storyId.get(count));
                            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progress.skip();
                                        progress.resume();
                                        Toast.makeText(StoryWindowActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                }
                            });

                        }
                    });

                    bottomSheetDialog.show();
                } else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StoryWindowActivity.this);
                    bottomSheetDialog.setContentView(R.layout.setting_story);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    progress.pause();
                    bottomSheetDialog.show();
                }
            }
        });

        settingsStoryBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StoryWindowActivity.this);
                    bottomSheetDialog.setContentView(R.layout.setting_story_my);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    progress.pause();

                    TextView deleteStory = bottomSheetDialog.findViewById(R.id.deleteStory);
                    deleteStory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                                    child("Story").child(userId).child(storyId.get(count));
                            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progress.skip();
                                        progress.resume();
                                        Toast.makeText(StoryWindowActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                }
                            });

                        }
                    });

                    bottomSheetDialog.show();
                }
            }
        });

        linearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryWindowActivity.this, StoryViewActivity.class);
                intent.putExtra("title", "Просмотры");
                intent.putExtra("id", userId);
                intent.putExtra("viewCon", contView);
                intent.putExtra("storyId", storyId.get(count));
                startActivity(intent);
            }
        });

        name_user_stories.setText(getIntent().getStringExtra("username"));
        Glide.with(getApplicationContext())
                .load(getIntent().getStringExtra("imageurl"))
                .into(stories_profale);

        getStories(userId);
    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                try {
                    Glide.with(StoryWindowActivity.this).load(user.getImageurl()).into(profileStory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getStories(String userId) {
        images = new ArrayList<>();
        storyId = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Story").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyId.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Stories stories = dataSnapshot.getValue(Stories.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > stories.getTimestart() && timecurrent < stories.getTimeend()) {
                        images.add(stories.getImageurl());
                        storyId.add(stories.getStoriesid());
                    }
                }

                progress.setStoriesCount(images.size());
                progress.setStoryDuration(5000L);
                progress.setStoriesListener(StoryWindowActivity.this);
                progress.startStories(count);
                Glide.with(getApplication()).load(images.get(count)).into(mainWindowStories);

                addView(storyId.get(count));
                seenNumber(storyId.get(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addView(String storyId) {
        FirebaseDatabase.getInstance().getReference("Story").
                child(userId).child(storyId).child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    private void seenNumber(String storyId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userId).child(storyId).child("views");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contView = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++count)).into(mainWindowStories);
        addView(storyId.get(count));
        seenNumber(storyId.get(count));
    }

    @Override
    public void onPrev() {
        if ((count - 1) < 0)
            return;
        Glide.with(getApplicationContext()).load(images.get(--count)).into(mainWindowStories);

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progress.resume();
    }
}