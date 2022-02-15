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
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.example.instagram.StoryWindowActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StorisAdapter extends RecyclerView.Adapter<StorisAdapter.ViewHolder> {

    private Context context;
    private List<Stories> storiesList;

    public StorisAdapter(Context context, List<Stories> storiesList) {
        this.context = context;
        this.storiesList = storiesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.storis_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stories stories = storiesList.get(position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(stories.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getImageurl().equals("default")) {
                            holder.prifale_storis.setImageResource(R.drawable.profilo);
                        } else {
                            try {
                                Glide.with(context)
                                        .load(user.getImageurl())
                                        .into(holder.prifale_storis);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        holder.name_storis.setText(user.getUsername());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, StoryWindowActivity.class);
                                intent.putExtra("username",user.getUsername());
                                intent.putExtra("imageurl",user.getImageurl());
                                intent.putExtra("storiesurl",stories.getImageurl());
                                context.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return storiesList.size();
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
