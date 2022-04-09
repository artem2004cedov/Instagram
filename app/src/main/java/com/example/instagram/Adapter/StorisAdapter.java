package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.AddStorisActivity;
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.example.instagram.Activity.StoryWindowActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        userInfo(holder, stories);
        seenStory(holder, stories.getPublisher());
    }

    private void userInfo(@NonNull ViewHolder holder, Stories stories) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(stories.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        try {
                            Glide.with(context).load(user.getImageurl()).into(holder.story_photo);
                        } catch (Exception e) {
                        }

                            if (user.getImageurl().equals("default")) {
                                holder.story_photo.setImageResource(R.drawable.profilo);
                            } else {
                                try {
                                    Glide.with(context).load(user.getImageurl()).into(holder.story_photo_seen);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            holder.user_name_story.setText(user.getUsername());


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, StoryWindowActivity.class);
                                intent.putExtra("username", user.getUsername());
                                intent.putExtra("imageurl", user.getImageurl());
                                intent.putExtra("storiesurl", stories.getImageurl());
                                intent.putExtra("userId", stories.getPublisher());
                                context.startActivity(intent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

//    private void myStories(TextView textView, ImageView imageView, boolean click) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Story")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int count = 0;
//                long timeCurrent = System.currentTimeMillis();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Stories stories = dataSnapshot.getValue(Stories.class);
//
//                    if (timeCurrent > stories.getTimestart() && stories.getTimeend() < timeCurrent) {
//                        count++;
//                    }
//
//                }
//                context.startActivity(new Intent(context, AddStorisActivity.class));
//
//
//                if (click) {
////                    if (count > 0) {
////                        context.startActivity(new Intent(context, AddStorisActivity.class));
////                    } else {
////                        context.startActivity(new Intent(context, AddStorisActivity.class));
////                    }
//                } else {
//                    if (count > 0) {
//                        textView.setText("Ваша история");
//                        imageView.setVisibility(View.GONE);
//                    } else {
//                        textView.setText("Добавить историю");
//                        imageView.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void seenStory(ViewHolder viewHolder, String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Story").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                            /*&& System.currentTimeMillis() < dataSnapshot.getValue(Stories.class).getTimeend()*/) {
                        i++;
                    }
                }

                if (i > 0) {
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView story_photo, story_photo_seen, add_story_photo;
        private TextView user_name_story, add_story_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            add_story_photo = itemView.findViewById(R.id.add_story_photo);
            user_name_story = itemView.findViewById(R.id.user_name_story);
            add_story_name = itemView.findViewById(R.id.add_story_name);
        }

    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return 0;
//        }
//        return 1;
//    }
}
