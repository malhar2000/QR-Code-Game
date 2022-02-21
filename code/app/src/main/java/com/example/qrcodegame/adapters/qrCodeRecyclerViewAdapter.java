package com.example.qrcodegame.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;

import java.util.ArrayList;


public class qrCodeRecyclerViewAdapter extends RecyclerView.Adapter<qrCodeRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> localDataSetQRCodeNames;
    private final ArrayList<String> localDataSetQRCodeScores;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtViewQRCodeName;
        private final TextView txtViewQRCodeScores;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            txtViewQRCodeName = (TextView) view.findViewById(R.id.eachQRCode);
            txtViewQRCodeScores = (TextView) view.findViewById(R.id.eachQRCodeScore);

        }

        public TextView getTextViewQRCodeName() {
            return txtViewQRCodeName;
        }
        public TextView getTxtViewQRCodeScore() {
            return txtViewQRCodeScores; }
        }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet Arraylist containing the data to populate views to be used
     * by RecyclerView.
     */
    public qrCodeRecyclerViewAdapter(ArrayList<String> dataSet, ArrayList<String> dataSet2) {
        localDataSetQRCodeNames = dataSet;
        localDataSetQRCodeScores = dataSet2;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.qr_code_recycler_view_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextViewQRCodeName().setText(localDataSetQRCodeNames.get(position));
        viewHolder.getTxtViewQRCodeScore().setText(localDataSetQRCodeScores.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSetQRCodeNames.size();
    }
}

