package com.example.instagram.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    static CircleImageView imageProfile;
    private ImageView save;
    private TextView changePhoto;
    private MaterialEditText fullname;
    private MaterialEditText username;
    private MaterialEditText bio;
    static List<User> listUri;
    static List<User> follList;

    private FirebaseUser fUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        imageProfile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        changePhoto = findViewById(R.id.change_photo);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        listUri = new ArrayList<>();
        follList = new ArrayList<>();
        MainActivity.onlineTame();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");

        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
//            return false;
            getimageuri();
        } else {
//            return true;
            FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    fullname.setText(user.getName());
                    username.setText(user.getUsername());
                    bio.setText(user.getBio());

                    getSharedPreferences("profaleUser", MODE_PRIVATE).edit().putString("name", user.getName()).apply();
                    getSharedPreferences("profaleUser", MODE_PRIVATE).edit().putString("username", user.getUsername()).apply();
                    getSharedPreferences("profaleUser", MODE_PRIVATE).edit().putString("bio", user.getBio()).apply();

                    if (user.getImageurl().equals("default")) {
                        imageProfile.setImageResource(R.drawable.profilo);
                    } else {

                        listUri.clear();
                        listUri.add(dataSnapshot.getValue(User.class));

                        getimageuri();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        for (User user1 : listUri) {
            Picasso.get().load(user1.getImageurl()).into(imageProfile);
            Toast.makeText(EditProfileActivity.this, "????????", Toast.LENGTH_SHORT).show();
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", fullname.getText().toString());
                map.put("username", username.getText().toString());
                map.put("bio", bio.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
                finish();
            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        dataCaching();

    }

    private void getimageuri() {
        follList.clear();
        follList.addAll(listUri);
        for (User user : follList) {
            Glide.with(getApplicationContext())
                    .load(user.getImageurl())
                    .into(imageProfile);
        }
    }

    private void dataCaching() {
        fullname.setText(getSharedPreferences("profaleUser", MODE_PRIVATE).getString("name", ""));
        username.setText(getSharedPreferences("profaleUser", MODE_PRIVATE).getString("username", ""));
        bio.setText(getSharedPreferences("profaleUser", MODE_PRIVATE).getString("bio", ""));
        imageProfile.setImageResource(R.drawable.profilo);
    }

    private void uploadImage() {
        final Dialog pd = new Dialog(this);
        pd.setContentView(R.layout.loading_window_addpost);
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.create();
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");

            uploadTask = fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageurl").setValue(url);
                        pd.dismiss();
                    } else {
                    }
                }
            });
        } else {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.offline();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.online();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();
        } else {
        }
    }

}
