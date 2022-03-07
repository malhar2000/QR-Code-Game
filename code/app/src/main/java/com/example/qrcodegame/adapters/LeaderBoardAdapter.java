package com.example.qrcodegame.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;
import com.example.qrcodegame.ViewProfileActivity;
import com.example.qrcodegame.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    private List<User> saveLeaderInfos = new ArrayList<>();
    private final Context mcontext;
    String[] mColors = {"#C2DFFF", "#C6DEFF", "#BDEDFF", "#B0E0E6", "#AFDCEC", "#ADD8E6", "#CFECEC",
            "#AAF0D1", "#99C68E", "#DBF9DB", "#FAEBD7", "#FFEFD5", "#FFE4C4", "#FDD7E4",
            "#FFE6E8", "#DCD0FF", "#FCDFFF", "#F8F6F0", "#FAF0DD", "#FBFBF9", "#FFFAFA",
            "#FEFCFF", "#FFF9E3", "#b83800", "#dd0244", "#c90000", "#465400",
            "#ff004d", "#ff6700", "#5d6eff", "#3955ff", "#0a24ff", "#004380", "#6b2e53",
            "#a5c996", "#f94fad", "#ff85bc", "#ff906b", "#b6bc68", "#296139"};
    Random random = new Random();

    public LeaderBoardAdapter(List<User> saveLeaderInfos, Context context) {
        this.saveLeaderInfos = saveLeaderInfos;
        this.mcontext = context;
        int size = saveLeaderInfos.size();
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
        holder.textViewUserName.setText(saveLeaderInfos.get(position).getUsername());
        holder.textViewUserScore.setText("" + saveLeaderInfos.get(position).getTotalScore());
        holder.textRank.setText("" + (position + 1));

        //For multiple colors inside the recycle View
        holder.parentLayout.setBackgroundColor(Color.parseColor(mColors[random.nextInt(40)]));
        holder.parentLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mcontext, ViewProfileActivity.class);
            intent.putExtra("username", saveLeaderInfos.get(position).getUsername());
            mcontext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return saveLeaderInfos.size();
    }

    public void filterList(ArrayList<User> fList){
        saveLeaderInfos = fList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

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
