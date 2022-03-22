package com.example.instagram.Interface;

import com.example.instagram.Model.Post;

import java.util.List;

public interface OnDataReceived {
    void onReceived(List<Post> postList);
}
