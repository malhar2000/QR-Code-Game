package com.example.qrcodegame.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;

import java.util.ArrayList;

public class QrUsernameAdapter extends RecyclerView.Adapter<QrUsernameAdapter.viewHolder>{

    private Context context;
    private ArrayList<String> usernames;

    public QrUsernameAdapter(ArrayList<String> usernames, Context context) {
        this.context = context;
        this.usernames = usernames;
    }

    @androidx.annotation.NonNull
    @Override
    public viewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.element_single_username, parent, false);
        return new QrUsernameAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull viewHolder holder, int position) {
        holder.username.setText(usernames.get(position));
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView username;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.elementUserNameTxt);
        }
    }
}
