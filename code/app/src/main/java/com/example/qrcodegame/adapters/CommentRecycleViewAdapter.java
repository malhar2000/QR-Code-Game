package com.example.qrcodegame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;

import java.util.ArrayList;
import java.util.List;

public class CommentRecycleViewAdapter extends RecyclerView.Adapter<CommentRecycleViewAdapter.ViewHolder> {

    List<String> list = new ArrayList<>();
    Context context;

    public CommentRecycleViewAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecycleViewAdapter.ViewHolder holder, int position) {
        holder.user_comment.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView user_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_comment = itemView.findViewById(R.id.user_comment);

        }


    }
}
