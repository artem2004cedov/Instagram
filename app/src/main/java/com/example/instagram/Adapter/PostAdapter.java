package com.example.instagram.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.CommentActivity;
import com.example.instagram.Activity.FollowersActivity;
import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;
    private List<Post> mPosts;

    int number_of_clicks = 0;
    boolean thread_started = false;
    final int DELAY_BETWEEN_CLICKS_IN_MILLISECONDS = 350;
    int num = 0;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {
        final Post post = mPosts.get(position);

        Glide.with(mContext)
                .load(post.getImageurl())
                .into(holder.postImage);

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        // Данные автора поста
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user.getImageurl().equals("default")) {
                            holder.imageProfile.setImageResource(R.drawable.profilo);
                        } else {
                            try {
                                Glide.with(mContext)
                                        .load(user.getImageurl())
                                        .into(holder.imageProfile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!post.getPublisher().equals(firebaseUser.getUid())) {
                            holder.textMy.setVisibility(View.VISIBLE);
                            holder.textMy.setText("Потому что вы подписаны на " + user.getUsername());

                        }
                        holder.author.setText(user.getUsername());
                        holder.username.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Date date = new Date(post.getTame());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-HH:mm");
        String strDate = simpleDateFormat.format(date);
        holder.textDate.setText(strDate.toString());

        isLiked(post.getPostid(), holder.like);
        noOfLikes(post.getPostid(), holder.noOfLikes);
        getComments(post.getPostid(), holder.noOfComments);
        isSaved(post.getPostid(), holder.save);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    // 1 Автор поста 2 текущий пользователь
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostid(), post.getPublisher());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        // Коменты
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });


        // Коменты
        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        // сохронёные
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        // переход в профиль
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        // переход в профиль
        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getPublisher().equals(FirebaseAuth.getInstance().getUid())) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                    bottomSheetDialog.setContentView(R.layout.window_setings);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    TextView deletPosttext = bottomSheetDialog.findViewById(R.id.deletPosttext);
                    deletPosttext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            Dialog dialog = new Dialog(mContext);
                            dialog.setContentView(R.layout.deletepost);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            TextView deleteText = dialog.findViewById(R.id.deleteText);
                            TextView deletepostback = dialog.findViewById(R.id.deletepostback);
                            deletepostback.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            deleteText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostid()).removeValue();
//                                    Toast.makeText(mContext, "Публикация удалена", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });
                    bottomSheetDialog.show();
                } else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                    bottomSheetDialog.setContentView(R.layout.window_setings_passerby);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    TextView passerbyhideText = bottomSheetDialog.findViewById(R.id.passerbyhideText);
                    passerbyhideText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            deletepost(position);
                        }
                    });
                    bottomSheetDialog.show();
                }
            }
        });

        // установка последнего сообщение
        FirebaseDatabase.getInstance().

                getReference().

                child("Comments")
                .

                        child(post.getPostid())
                .

                        orderByChild("timestamp")
                .

                        limitToLast(1).

                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                holder.commentLast.setText(dataSnapshot.child("comment").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++number_of_clicks;
                setAnimation(holder.heartAnimation, number_of_clicks);
                if (!thread_started) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            thread_started = true;
//                            +79634164421
                            try {
                                Thread.sleep(DELAY_BETWEEN_CLICKS_IN_MILLISECONDS);
                                if (number_of_clicks == 1) {
                                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();
                                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new PostDetailFragment()).commit();

                                } else if (number_of_clicks == 2) {
                                    if (holder.like.getTag().equals("like")) {
                                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                                .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                                        addNotification(post.getPostid(), post.getPublisher());

                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                                .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                                    }
                                }
                                number_of_clicks = 0;
                                thread_started = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }
            }
        });

        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "Отметки\"Нравится\"");
                mContext.startActivity(intent);
            }
        });
    }

    private void deletepost(int position) {
        mPosts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPosts.size());
    }

    private void setAnimation(ImageView heartAnimation, int number_of_clicks) {
        Animation animHide = AnimationUtils.loadAnimation(mContext, R.anim.button_anim_hide);
        Animation animShow = AnimationUtils.loadAnimation(mContext, R.anim.button_anim_show);
        if (number_of_clicks == 2) {
            heartAnimation.setVisibility(View.VISIBLE);
            heartAnimation.setAnimation(animShow);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    heartAnimation.setVisibility(View.GONE);
                    heartAnimation.setAnimation(animHide);
                }
            }, 500);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public ImageView imageProfile, profale_coment;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more, heartAnimation;
        public SocialAutoCompleteTextView put_comment;

        public TextView username, textDate;
        public TextView noOfLikes, deletPosttext;
        public TextView author, commentLast, textMy;
        public TextView noOfComments;
        public LinearLayout linearsetibgs;
        SocialTextView description;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            textMy = itemView.findViewById(R.id.textMy);
            commentLast = itemView.findViewById(R.id.commentLast);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            deletPosttext = itemView.findViewById(R.id.deletPosttext);
            more = itemView.findViewById(R.id.more);
            textDate = itemView.findViewById(R.id.textDate);
            heartAnimation = itemView.findViewById(R.id.heartAnimation);


            username = itemView.findViewById(R.id.username);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            author = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            description = itemView.findViewById(R.id.description);

        }
    }

    private void isSaved(final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    // если у меня есть то сохроннёные
                    image.setImageResource(R.drawable.saveiconback);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.saveicon);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.heart);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void noOfLikes(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText("Нравится: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComments(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText("Смотреть все комментирии (" + dataSnapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // id поста id автора
    private void addNotification(String postId, String publisherId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notificationId = databaseReference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("notifid", notificationId);
        map.put("text", "Понравилось ваше фото.");
        map.put("postid", postId);
        map.put("isPost", true);

        databaseReference.child(publisherId).child(notificationId).setValue(map);
    }

}
