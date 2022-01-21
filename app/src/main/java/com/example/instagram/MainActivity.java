package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.PostAdapter;
import com.example.instagram.Fragments.HomeFragment;
import com.example.instagram.Fragments.NotificationFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Fragments.SearchFragment;
import com.example.instagram.Login.VxotActivity;
import com.example.instagram.Model.Notification;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {

    private Fragment selectorFragment;
    private ImageView homeMain;
    private ImageView searchMain;
    private TextView textnotifications;
    private ImageView addMain;
    private ImageView heartMani;
    private CircleImageView profaleMani;
    private FirebaseUser fUser;

    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 101;
    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchMain = findViewById(R.id.searchMain);
        textnotifications = findViewById(R.id.textnotifications);
        homeMain = findViewById(R.id.homeMain);
        addMain = findViewById(R.id.addMain);
        heartMani = findViewById(R.id.heartMani);
        profaleMani = findViewById(R.id.profaleMani);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        readUser();
        readNotifications();

        homeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorFragment = new HomeFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
            }
        });

        searchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorFragment = new SearchFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
            }
        });

        addMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        heartMani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textnotifications.setVisibility(View.INVISIBLE);
                selectorFragment = new NotificationFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
            }
        });

        profaleMani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorFragment = new ProfileFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
            }
        });



        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private void readUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getImageurl().equals("default")) {
                    profaleMani.setImageResource(R.drawable.profilo);
                } else {
                    if (getApplicationContext() != null)
                        Glide.with(getApplicationContext())
                                .load(user.getImageurl())
                                .into(profaleMani);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readNotifications() {
        List<Notification> notificationList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Notification notification = dataSnapshot1.getValue(Notification.class);
                    notificationList.add(notification);
                }
                for (Notification notification : notificationList) {
                   phone_notification(notification);
                }


                int num = (int) dataSnapshot.getChildrenCount();
                int num2 = num;
                num2++;
                if (num != 0) {
                    if (num < num2) {
                        textnotifications.setVisibility(View.VISIBLE);
                    } else {
                        textnotifications.setVisibility(View.INVISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void phone_notification(Notification notification) {
        final String[] ur = new String[1];
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Intent notificationIntent = new Intent(this, SplashScreenActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);



        FirebaseDatabase.getInstance().getReference().child("Users").child(notification.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Установка большой картинки
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrowicon, options);

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setAutoCancel(false)
                                .setSmallIcon(R.drawable.iconinst) /* Установка значка уведлений*/
                                .setWhen(System.currentTimeMillis()) /* Время кведолмения*/
                                .setContentIntent(contentIntent) /* При нажати перехот в активность*/
                                .setContentTitle("Instagram") /*Заголовок*/
                                .setContentText(user.getName() + " " +  notification.getText()) /* Текст*/
//                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)) /* Установка большой картинки */
                                .setSound(defaultSoundUri) /* Установка звука*/
                                .setAutoCancel(true) /* Авто закрытие после нажатия*/
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.profilo)) /* Установка значка уведомления*/
//                        .addAction(R.drawable.pfofaleicon,"Запуск",contentIntent) /* Нижняя надпись*/
//                        .setStyle(new NotificationCompat.InboxStyle()
//                                .addLine("Line 1")
//                                .addLine("Line 2")
//                                .addLine("Line 3")
//                                .setBigContentTitle("Extended title")
//                                .setSummaryText("+5 more")) /* Сообощения в столбик*/
                                .setPriority(PRIORITY_HIGH); /* Устанока приоретета*/

                FirebaseDatabase.getInstance().getReference().child("Posts").child(notification.getPostid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        ur[0] = post.getImageurl();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                createChannelIfNeeded(notificationManager);
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        // Установка большой картинки
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrowicon, options);
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                        .setAutoCancel(false)
//                        .setSmallIcon(R.drawable.iconinst) /* Установка значка уведлений*/
//                        .setWhen(System.currentTimeMillis()) /* Время кведолмения*/
//                        .setContentIntent(contentIntent) /* При нажати перехот в активность*/
//                        .setContentTitle("Title") /*Заголовок*/
//                        .setContentText("text") /* Текст*/
//                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)) /* Установка большой картинки */
//                        .setSound(defaultSoundUri) /* Установка звука*/
//                        .setAutoCancel(true) /* Авто закрытие после нажатия*/
////                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.profilo)) /* Установка значка уведомления*/
////                        .addAction(R.drawable.pfofaleicon,"Запуск",contentIntent) /* Нижняя надпись*/
////                        .setStyle(new NotificationCompat.InboxStyle()
////                                .addLine("Line 1")
////                                .addLine("Line 2")
////                                .addLine("Line 3")
////                                .setBigContentTitle("Extended title")
////                                .setSummaryText("+5 more")) /* Сообощения в столбик*/
//                        .setPriority(PRIORITY_HIGH); /* Устанока приоретета*/

//        createChannelIfNeeded(notificationManager);
//        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
