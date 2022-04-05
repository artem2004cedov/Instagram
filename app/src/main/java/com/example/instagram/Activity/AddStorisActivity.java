package com.example.instagram.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStorisActivity extends AppCompatActivity {

    private Uri imageUri;
    private String imageUrl;
    private ImageView image_foto, image_Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_storis);
        image_foto = findViewById(R.id.image_foto);
        image_Next = findViewById(R.id.image_Next);

        CropImage.activity().start(AddStorisActivity.this);

        findViewById(R.id.arrowBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    // при нажатии на продолжить
    private void upload() {
        final Dialog pd = new Dialog(this);
        pd.setContentView(R.layout.loading_window_addpost);
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.create();
        pd.show();
        // если есть картинки
        if (imageUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().
                    getReference("Stories").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadtask = filePath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Story");
                    String storiesid = ref.push().getKey();
                    long timeend = System.currentTimeMillis() + 86400000;

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("storiesid", storiesid);
                    map.put("imageurl", imageUrl);
                    map.put("timestart", ServerValue.TIMESTAMP);
                    map.put("timeend", timeend);
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(storiesid).setValue(map);

                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(AddStorisActivity.this,MainActivity.class));
                                finish();
                            }
                        },600);

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } else {
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    // открыват фото
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // записывается id картинки
            imageUri = result.getUri();
            image_foto.setImageURI(imageUri);
        } else {
            startActivity(new Intent(AddStorisActivity.this, MainActivity.class));
            finish();
        }
    }
}