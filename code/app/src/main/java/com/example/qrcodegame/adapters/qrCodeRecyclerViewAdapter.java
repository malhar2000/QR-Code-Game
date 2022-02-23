package com.example.qrcodegame.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;
import com.example.qrcodegame.SingleQRActivity;

import java.util.ArrayList;


public class qrCodeRecyclerViewAdapter extends RecyclerView.Adapter<qrCodeRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> localDataSetQRCodeNames;
    private final ArrayList<String> localDataSetQRCodeScores;
    private final QRProfileListener listener;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ConstraintLayout layout;
        private final TextView txtViewQRCodeName;
        private final TextView txtViewQRCodeScores;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            layout = (ConstraintLayout) view.findViewById(R.id.qrCodeInProfileLayout);
            txtViewQRCodeName = (TextView) view.findViewById(R.id.eachQRCode);
            txtViewQRCodeScores = (TextView) view.findViewById(R.id.eachQRCodeScore);

        }

        public TextView getTextViewQRCodeName() {
            return txtViewQRCodeName;
        }
        public TextView getTxtViewQRCodeScore() {
            return txtViewQRCodeScores; }
        }

        public interface QRProfileListener {
            void onQRclicked(String qrId);
        }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet Arraylist containing the data to populate views to be used
     * by RecyclerView.
     */
    public qrCodeRecyclerViewAdapter(ArrayList<String> dataSet, ArrayList<String> dataSet2, Context context1, QRProfileListener listener1) {
        localDataSetQRCodeNames = dataSet;
        localDataSetQRCodeScores = dataSet2;
        context = context1;
        listener = listener1;
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
        viewHolder.getTextViewQRCodeName().setText("Name: " + localDataSetQRCodeNames.get(position));
        viewHolder.getTxtViewQRCodeScore().setText(localDataSetQRCodeScores.get(position));
        viewHolder.layout.setOnClickListener(view -> {
            listener.onQRclicked(localDataSetQRCodeNames.get(position));
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSetQRCodeNames.size();
    }
}

