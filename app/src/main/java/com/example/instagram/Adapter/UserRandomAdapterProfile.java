package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.MainActivity;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRandomAdapterProfile extends RecyclerView.Adapter<UserRandomAdapterProfile.ViewHolder> {

    private Context context;
    private List<User> userList;
    private boolean isFargment;

    private FirebaseUser firebaseUser;

    public UserRandomAdapterProfile(Context context, List<User> userList, boolean isFargment) {
        this.context = context;
        this.userList = userList;
        this.isFargment = isFargment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.random_itemprofile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = userList.get(position);

        holder.nameRandom.setText(user.getUsername());
        holder.btn_RandomFollow.setVisibility(View.VISIBLE);
        if (user.getImageurl().equals("default")) {
            holder.imageRandom.setImageResource(R.drawable.profilo);
        } else {
            Picasso.get().load(user.getImageurl()).into(holder.imageRandom);
        }
        isFollowed(user.getId(), holder.btn_RandomFollow);


        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btn_RandomFollow.setVisibility(View.GONE);
        }


        holder.btn_RandomFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // если нажать пописаться
                if (holder.btn_RandomFollow.getText().toString().equals(("Подписаться"))) {

                    // нынешний пользователь
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).setValue(true);

                    // подписчик
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFargment) {
                    context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id
                            .fragment_container, new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("publisherId", user.getId());
                    context.startActivity(intent);
                }
            }
        });

        holder.imageRemovProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removItem(position);
            }
        });


    }

    private void removItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,userList.size());
    }

    private void isFollowed(final String id, final Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    btnFollow.setText("Отписаться");
                    btnFollow.setBackgroundResource(R.drawable.buuton_bio);
                    btnFollow.setTextColor(Color.BLACK);
                }else {
                    btnFollow.setText("Подписаться");
                    btnFollow.setBackgroundResource(R.drawable.buuton_folovers);
                    btnFollow.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameRandom;
        private Button btn_RandomFollow;
        private ImageView imageRemovProfile;
        private CircleImageView imageRandom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageRemovProfile = itemView.findViewById(R.id.imageRemovProfile);
            imageRandom = itemView.findViewById(R.id.imageRandom);
            nameRandom = itemView.findViewById(R.id.nameRandom);
            btn_RandomFollow = itemView.findViewById(R.id.btn_RandomFollow);
        }
    }

    private void addNotification(String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", userId);
        map.put("text", "Подписался(-ась) на ваши обновления. ");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }
}
