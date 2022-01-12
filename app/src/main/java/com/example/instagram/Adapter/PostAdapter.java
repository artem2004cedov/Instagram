package com.example.instagram.Adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.CommentActivity;
import com.example.instagram.FollowersActivity;
import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.MainActivity;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;
    private List<Post> mPosts;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 101;
    private static final String CHANNEL_ID = "CHANNEL_ID";


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
        Picasso.get().load(post.getImageurl()).into(holder.postImage);

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
                            Picasso.get().load(user.getImageurl()).placeholder(R.drawable.profilo).into(holder.imageProfile);
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
//                    phone_notification();
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

        // установка последнего сообщение
        FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(post.getPostid())
                .orderByChild("timestamp")
                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
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


//        holder.postImage.setOnClickListener(new View.OnClickListener() {
//            int i = 0;
//
//            @Override
//            public void onClick(View v) {
//                i++;
//                Handler handler = new Handler();
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
//                        i = 0;
//                    }
//                };
//                if (i == 1) {
//                    handler.postDelayed(r, 100);
//                } else if (i == 2) {
//                    Toast.makeText(mContext, "2 нажатия", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPublisher());
                intent.putExtra("title", "Отметки\"Нравится\"");
                mContext.startActivity(intent);
            }
        });

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
        public ImageView more;
        public SocialAutoCompleteTextView put_comment;

        public TextView username, textDate;
        public TextView noOfLikes;
        public TextView author, commentLast, textMy;
        public TextView noOfComments;
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
            more = itemView.findViewById(R.id.more);
            textDate = itemView.findViewById(R.id.textDate);

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
        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", publisherId);
        map.put("text", "Понравилось ваше фото.");
        map.put("postid", postId);
        map.put("isPost", true);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }

//
//    private void phone_notification() {
//        Intent intent = new Intent(mContext, PostAdapter.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        Intent notificationIntent = new Intent(mContext, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
//                0, notificationIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//
//        NotificationCompat.Builder notifaketionBilder =
//                new NotificationCompat.Builder(mContext, CHANNEL_ID)
//                        .setAutoCancel(false) // астоматическая отмена
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setWhen(System.currentTimeMillis())
//                        .setContentTitle("Заголовок")
//                        .setContentText("Текст")
//                        .setContentIntent(contentIntent) // запуск при нажатии
////                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
////                                R.drawable.profilo)) // большая картинка
//                        .addAction(R.drawable.ic_launcher_foreground, "Запустить активность",
//                                contentIntent)  // запуск при нажатии
//                        .setAutoCancel(true) // автоматически закрыть уведомление после нажатия
//                        .setPriority(PRIORITY_HIGH);
//        createChannelIfNeeded(notificationManager);
//        notificationManager.notify(NOTIFY_ID, notifaketionBilder.build());
//    }

//    public static void createChannelIfNeeded(NotificationManager manager) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new
//                    NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
//            manager.createNotificationChannel(notificationChannel);
//        }
//    }

}
