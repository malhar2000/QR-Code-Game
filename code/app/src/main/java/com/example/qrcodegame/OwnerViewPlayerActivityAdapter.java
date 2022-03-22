package com.example.qrcodegame;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class OwnerViewPlayerActivityAdapter extends RecyclerView.Adapter<OwnerViewPlayerActivityAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<User> list;
    static ArrayList<User> listAll;
    String[] mColors = {
            "#AAF0D1", "#99C68E", "#DBF9DB", "#FAEBD7", "#FFEFD5", "#FFE4C4", "#FDD7E4",
            "#FFE6E8", "#DCD0FF", "#FCDFFF", "#F8F6F0", "#FAF0DD", "#FBFBF9", "#FFFAFA",
            "#FEFCFF", "#FFF9E3", "#b83800", "#dd0244", "#c90000", "#465400",
    };
    Random random = new Random();

    public OwnerViewPlayerActivityAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
        listAll = new ArrayList<>(list);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.owner_players_list,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.user_name.setText(list.get(position).getUsername());
        holder.score.setText(String.valueOf(list.get(position).getTotalScore()));
        holder.cardView.setCardBackgroundColor(Color.parseColor(mColors[random.nextInt(20)]));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<User> filterList = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filterList.addAll(listAll);
            }else{
                for(User name: listAll){
                    if(name.getUsername().contains(charSequence.toString())){
                        filterList.add(name);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends User>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView user_name, score;
        CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            score = itemView.findViewById(R.id.score);
            cardView = itemView.findViewById(R.id.player_list_cardView);
        }
    }

}
