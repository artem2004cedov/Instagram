package com.example.instagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.MassegeModel;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CatheAdapter extends RecyclerView.Adapter {

    private ArrayList<MassegeModel> massegeModel;
    private Context context;
    private String recId;

    private int SENDER_VIEW_TYPE = 1;
    private int RECEVER_VIEW_TYPE = 2;


    public CatheAdapter(ArrayList<MassegeModel> massegeModel, Context context, String recId) {
        this.massegeModel = massegeModel;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            // отправитель то рисуй на право
            View view = LayoutInflater.from(context).inflate(R.layout.simpl_sender, parent, false);
            return new SenderHolder(view);
        } else {
            // Получатель то рисуй на слево
            View view = LayoutInflater.from(context).inflate(R.layout.simple_recever, parent, false);
            return new RecenverHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
//        берет с арей листа id и сравнивает его с пользователем
        if (massegeModel.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            // если совпадает то это оптравитель
            return SENDER_VIEW_TYPE;
        } else {
            // если не свопадает то это получатель
            return RECEVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MassegeModel massegeMod = massegeModel.get(position);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new AlertDialog.Builder(context)
//                        .setTitle("Удалить сообщение ?")
//                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                // recId рандом
//                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
//                                database.getReference().child("chats").child(senderRoom)
//                                        .child(massegeMod.getMassageId()).removeValue();
//
//                            }
//                        }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//            }
//
//        });


        if (holder.getClass() == SenderHolder.class) {
            // записывам сообщение
            ((SenderHolder) holder).text_sender_massege.setText(massegeMod.getMessage());

            Date date = new Date(massegeMod.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String strDate = simpleDateFormat.format(date);
            ((SenderHolder) holder).text_sender_tame.setText(strDate.toString());

        } else {
            ((RecenverHolder) holder).text_recever_massege.setText(massegeMod.getMessage());

            Date date = new Date(massegeMod.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String strDate = simpleDateFormat.format(date);
            ((RecenverHolder) holder).text_recever_tame.setText(strDate.toString());

        }
    }

    @Override
    public int getItemCount() {
        return massegeModel.size();
    }

    public class RecenverHolder extends RecyclerView.ViewHolder {
        TextView text_recever_massege, text_recever_tame;

        public RecenverHolder(@NonNull View itemView) {
            super(itemView);

            text_recever_massege = itemView.findViewById(R.id.text_recever_massege);
            text_recever_tame = itemView.findViewById(R.id.text_recever_tame);
        }
    }

    public class SenderHolder extends RecyclerView.ViewHolder {
        TextView text_sender_massege, text_sender_tame;

        public SenderHolder(@NonNull View itemView) {
            super(itemView);
            text_sender_massege = itemView.findViewById(R.id.text_sender_massege);
            text_sender_tame = itemView.findViewById(R.id.text_sender_tame);
        }
    }
}