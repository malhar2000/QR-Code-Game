package com.example.qrcodegame.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Adapter for the comments recycler view
 */
public class CommentRecycleViewAdapter extends RecyclerView.Adapter<CommentRecycleViewAdapter.ViewHolder> {

    //Holds all the comments to display
    List<String> list;
    Context context;

    //This is only temp. will be changed later
    String[] mColors = {"#C2DFFF", "#C6DEFF", "#BDEDFF", "#B0E0E6", "#AFDCEC", "#ADD8E6", "#CFECEC",
            "#AAF0D1", "#99C68E", "#DBF9DB", "#FAEBD7", "#FFEFD5", "#FFE4C4", "#FDD7E4",
            "#FFE6E8", "#DCD0FF", "#FCDFFF", "#F8F6F0", "#FAF0DD", "#FBFBF9", "#FFFAFA",
            "#FEFCFF", "#FFF9E3", "#b83800", "#dd0244", "#c90000", "#465400",
            "#ff004d", "#ff6700", "#5d6eff", "#3955ff", "#0a24ff", "#004380", "#6b2e53",
            "#a5c996", "#f94fad", "#ff85bc", "#ff906b", "#b6bc68", "#296139"};
    Random random = new Random();

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
        holder.parentLayout.setCardBackgroundColor(Color.parseColor(mColors[random.nextInt(40)]));
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
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_comment = itemView.findViewById(R.id.user_comment);
            parentLayout = itemView.findViewById(R.id.cardViewComment);
        }


    }
}
