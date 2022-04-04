package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.ChatActivity;
import com.example.instagram.Activity.ChatUsersActivity;
import com.example.instagram.Activity.StoryWindowActivity;
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPepleOnlaneAdapter extends RecyclerView.Adapter<ChatPepleOnlaneAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public ChatPepleOnlaneAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.online_peple_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.name_storis.setText(user.getUsername());
        Glide.with(context).load(user.getImageurl()).error(R.drawable.profilo).into(holder.prifale_storis);

        // передача инфи о пользоватеи
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("useridrandom", user.getUseridraidom());
                intent.putExtra("profailPic", user.getImageurl());
                intent.putExtra("username", user.getUsername());
                intent.putExtra("name", user.getName());
                intent.putExtra("userid", user.getId());
                intent.putExtra("userstatus", user.getStatus());
                context.startActivity(intent);

                Map<String, Object> map = new HashMap<>();
                map.put("status", "Сейчас в сети");
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView prifale_storis;
        private TextView name_storis;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prifale_storis = itemView.findViewById(R.id.prifale_storis);
            name_storis = itemView.findViewById(R.id.name_storis);
        }

    }
}
