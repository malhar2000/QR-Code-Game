package com.example.qrcodegame.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;
import com.example.qrcodegame.SaveLeaderInfo;
import com.example.qrcodegame.ViewProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    private List<SaveLeaderInfo> saveLeaderInfos = new ArrayList<>();
    private Context mcontext;
    private int size;


    public LeaderBoardAdapter(List<SaveLeaderInfo> saveLeaderInfos, Context context) {
        this.saveLeaderInfos = saveLeaderInfos;
        this.mcontext = context;
        size = saveLeaderInfos.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.
                layout_for_leaderboard_recycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textViewUserName.setText(saveLeaderInfos.get(position).getUserName());
        holder.textViewUserScore.setText(saveLeaderInfos.get(position).getScore());
        holder.textRank.setText(saveLeaderInfos.get(position).getNum());

        holder.parentLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mcontext, ViewProfileActivity.class);
            intent.putExtra("username", saveLeaderInfos.get(position).getUserName());
            mcontext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return saveLeaderInfos.size();
    }

    public void filterList(ArrayList<SaveLeaderInfo> fList){
        saveLeaderInfos = fList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewUserScore;
        TextView textViewUserName;
        RelativeLayout parentLayout;
        TextView textRank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserScore = itemView.findViewById(R.id.textViewScore);
            textViewUserName = itemView.findViewById(R.id.userNameLeaderBoard);
            parentLayout = itemView.findViewById(R.id.userInLeaderBoardLayout);
            textRank = itemView.findViewById(R.id.textViewLeaderBoardRank);
        }


    }
}