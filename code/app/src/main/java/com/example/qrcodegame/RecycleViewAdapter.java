package com.example.qrcodegame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<Temp> temps = new ArrayList<>();
    private Context mcontext;
    private int size;


    public RecycleViewAdapter(List<Temp> temps, Context context) {
        this.temps = temps;
        this.mcontext = context;
        size = temps.size();
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
        holder.textViewUserName.setText(temps.get(position).getUserName());
        holder.textViewUserScore.setText(temps.get(position).getScore());
        holder.textRank.setText(temps.get(position).getNum());

        //String.valueOf(position+1)

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mcontext, temps.get(position).getUserName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return temps.size();
    }

    public void filterList(ArrayList<Temp> fList){
        temps = fList;
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
            parentLayout = itemView.findViewById(R.id.parent_layout);
            textRank = itemView.findViewById(R.id.textViewLeaderBoardRank);
        }


    }
}
