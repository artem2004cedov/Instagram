package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class StartRandomUserAdapter extends RecyclerView.Adapter<StartRandomUserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFargment;

    private FirebaseUser firebaseUser;

    public StartRandomUserAdapter(Context mContext, List<User> mUsers, boolean isFargment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFargment = isFargment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.start_recomendat_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        // делает кнопку падписаться видимой
        holder.randombtn_follow.setVisibility(View.VISIBLE);
        // устанавливаем время
        holder.startrandomusername.setText(user.getUsername());
        holder.startrandomfullname.setText(user.getName());
        Picasso.get().load(user.getImageurl()).placeholder(R.drawable.profilo).into(holder.image_profile_random);

        // проверка если базе пользователь
        isFollowed(user.getId(), holder.randombtn_follow);

        holder.randombtn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // если нажать пописаться
                if (holder.randombtn_follow.getText().toString().equals(("Подписаться"))) {

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

        // artem
        if (user.getId().equals("lnosYnOZz9MNEyo9Wmru4WheqzC2")) {
            holder.startRecomendatetRandom.setVisibility(View.VISIBLE);
        } else {
            holder.startRecomendatetRandom.setVisibility(View.GONE);
        }

        holder.startRandomClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFargment) {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id
                            .fragment_container, new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherId", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    private void removeItem(int position) {
        mUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mUsers.size());
    }

    private void isFollowed(final String id, final Button randombtn_follow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists())
                    randombtn_follow.setText("Отписаться");
                else
                    randombtn_follow.setText("Подписаться");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile_random;
        public TextView startrandomusername;
        public ImageView startRandomClose, startRecomendatetRandom;
        public TextView startrandomfullname;
        public Button randombtn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile_random = itemView.findViewById(R.id.image_profile_random);
            startRandomClose = itemView.findViewById(R.id.startRandomClose);
            startrandomusername = itemView.findViewById(R.id.startrandomusername);
            startRecomendatetRandom = itemView.findViewById(R.id.startRecomendatetRandom);
            startrandomfullname = itemView.findViewById(R.id.startrandomfullname);
            randombtn_follow = itemView.findViewById(R.id.randombtn_follow);
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
