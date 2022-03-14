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

    //Holds all the comments to display
    List<String> list;
    Context context;

    /**
     *
     * @param list content all the comments for a particular QRCode
     * @param context The data passed as for the RecycleView
     */
    public CommentRecycleViewAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    /**
     *
     * @param parent default parameters of override function.
     * @param viewType
     * @return a viewHolder for the recycleView
     */
    @NonNull
    @Override
    public CommentRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment_display, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method bind or display all the comments for particular qrcode on the display
     * @param holder holder is the holder for the view elements on recycleView
     * @param position adjust the positions to display of the recycle view on the when User scrolls
     *                 or adjust the comments.
     */
    @Override
    public void onBindViewHolder(@NonNull CommentRecycleViewAdapter.ViewHolder holder, int position) {
        holder.user_comment.setText(list.get(position));
    }

    /**
     *
     * @return the size of the list that content all the comments
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * ViewHolder class for the recycleView
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView user_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_comment = itemView.findViewById(R.id.user_comment);

        }


    }
}
