package com.example.qrcodegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class playerAdapter extends RecyclerView.Adapter<playerAdapter.MyViewHolder> {

    Context context;

    ArrayList<Player_data> list;

    public playerAdapter(Context context, ArrayList<Player_data> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.owner_players_list,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Player_data player_data = list.get(position);
        holder.user_name.setText(player_data.getPlayer_username());
        holder.score.setText(player_data.getScore());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView user_name, score;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            score = itemView.findViewById(R.id.score);

        }
    }

}
