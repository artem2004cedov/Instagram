package com.example.instagram.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.instagram.R;

public class StartUserRandomActivity extends AppCompatActivity {
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_user_random);

        fragment = new StartRecommendationFragmeint();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLRandom,fragment).commit();
    }
}