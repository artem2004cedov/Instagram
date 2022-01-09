package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 Класс для просмотра строз
 */

public class StoryWindowActivity extends AppCompatActivity {
    private ImageView mainWindowStories,add_comment_stories;
    private CircleImageView stories_profale;
    private TextView name_user_stories;
    private TextInputEditText massege_stories;
    private int count = 0;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_window);

        mainWindowStories = findViewById(R.id.mainWindowStories);
        stories_profale = findViewById(R.id.stories_profale);
        name_user_stories = findViewById(R.id.name_user_stories);
        massege_stories = findViewById(R.id.massege_stories);
        add_comment_stories = findViewById(R.id.add_comment_stories);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));

        // передача данных
        name_user_stories.setText(getIntent().getStringExtra("username"));
        Picasso.get().load(getIntent().getStringExtra("imageurl")).into(stories_profale);
        Picasso.get().load(getIntent().getStringExtra("storiesurl")).into(mainWindowStories);
        progres();
    }


    private void progres() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                progressBar.setProgress(count);
                if (count == 100) {
                    timer.cancel();
                    startActivity(new Intent(StoryWindowActivity.this,MainActivity.class));
                }
            }
        };
        timer.schedule(timerTask,0,40);
    }
}