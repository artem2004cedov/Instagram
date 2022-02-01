package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.ChatActivity;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public ChatUserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_hath_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        Date date = new Date(user.getStatustame());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-HH:mm");
        String strDate = simpleDateFormat.format(date);

        holder.text_name_item.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.drawable.profilo).into(holder.profile_that);

        if (user.getStatus().equals("Сейчас в сети")) {
            holder.statusText.setText(user.getStatus());
            holder.thatDotUser.setVisibility(View.VISIBLE);
        } else {
            holder.thatDotUser.setVisibility(View.GONE);
            holder.statusText.setText("Был(-а) в сети " + strDate.toString());
        }

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
        public CircleImageView profile_that, thatDotUser;
        public TextView text_name_item, statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_that = itemView.findViewById(R.id.profile_that);
            text_name_item = itemView.findViewById(R.id.text_name_item);
            statusText = itemView.findViewById(R.id.statusText);
            thatDotUser = itemView.findViewById(R.id.thatDotUser);
        }
    }
}
