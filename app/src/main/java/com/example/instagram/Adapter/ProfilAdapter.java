package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Activity.EditProfileActivity;
import com.example.instagram.Fragments.InterestingPeopleFragment;
import com.example.instagram.Model.Profil;
import com.example.instagram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilAdapter extends RecyclerView.Adapter<ProfilAdapter.ViewHolder> {

    private Context context;
    private List<Profil> profilList;

    private Fragment fragment;

    public ProfilAdapter(Context context, List<Profil> profilList) {
        this.context = context;
        this.profilList = profilList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profil, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int imageId = context.getResources().getIdentifier(profilList.get(position).getImage(), "drawable", context.getOpPackageName());
        int imageBagraund = context.getResources().getIdentifier(profilList.get(position).getBtcolor(), "drawable", context.getOpPackageName());

        holder.text_1.setText(profilList.get(position).getText1());
        holder.text_2.setText(profilList.get(position).getText2());
        holder.text_3.setText(profilList.get(position).getText3());
        holder.bttext.setText(profilList.get(position).getBttext());
        holder.bttext.setBackgroundResource(imageBagraund);
        holder.userProf.setImageResource(imageId);
        holder.bttext.setTextColor(Color.parseColor(profilList.get(position).getBttextciolor()));

        holder.bttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profilList.get(position).getId() == 1) {
                    context.startActivity(new Intent(context, EditProfileActivity.class));
                } else if (profilList.get(position).getId() == 2) {
                    context.startActivity(new Intent(context, EditProfileActivity.class));
                } else if (profilList.get(position).getId() == 3) {
                    context.startActivity(new Intent(context, EditProfileActivity.class));
                } else if (profilList.get(position).getId() == 4) {
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.
                            fragment_container, new InterestingPeopleFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return profilList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userProf;
        public TextView text_1, text_2, text_3;
        private Button bttext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProf = itemView.findViewById(R.id.userProf);
            text_1 = itemView.findViewById(R.id.text_1);
            text_2 = itemView.findViewById(R.id.text_2);
            text_3 = itemView.findViewById(R.id.text_3);
            bttext = itemView.findViewById(R.id.bttext);
        }
    }
}
