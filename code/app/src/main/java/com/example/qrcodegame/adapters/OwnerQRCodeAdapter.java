package com.example.qrcodegame.adapters;


import android.content.Context;
import androidx.annotation.NonNull;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;
import com.example.qrcodegame.models.QRCode;

import java.util.ArrayList;
import java.util.Random;

/**
 * Adapter for the owner activities' qr codes
 */
public class OwnerQRCodeAdapter extends RecyclerView.Adapter<OwnerQRCodeAdapter.viewHolder>{

    private ArrayList<QRCode> qrCodes;
    String[] mColors = {
            "#AAF0D1", "#99C68E", "#DBF9DB", "#FAEBD7", "#FFEFD5", "#FFE4C4", "#FDD7E4",
            "#FFE6E8", "#DCD0FF", "#FCDFFF", "#F8F6F0", "#FAF0DD", "#FBFBF9", "#FFFAFA",
            "#FEFCFF", "#FFF9E3", "#b83800", "#dd0244", "#c90000", "#465400",
    };
    private Random random = new Random();

    public OwnerQRCodeAdapter(ArrayList<QRCode> qrCodes) {
        this.qrCodes = qrCodes;
    }

    @androidx.annotation.NonNull
    @Override
    public viewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_owner_qr_code, parent, false);
        return new OwnerQRCodeAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull viewHolder holder, int position) {
        QRCode currentQRCode = qrCodes.get(position);

        holder.qrCodeId.setText("ID: " + currentQRCode.getId());
        if (currentQRCode.getCoordinates().size() == 0) {
            holder.qrCodeLocation.setText("Location: No Location!");
        } else {
            holder.qrCodeLocation.setText(String.format("Location: %f %f", currentQRCode.getCoordinates().get(0), currentQRCode.getCoordinates().get(1)));
        }
        holder.qrCodeWorth.setText("Worth: "+currentQRCode.getWorth());
        holder.cardView.setCardBackgroundColor(Color.parseColor(mColors[random.nextInt(20)]));

    }

    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView qrCodeId;
        TextView qrCodeLocation;
        TextView qrCodeWorth;
        CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeId = itemView.findViewById(R.id.QRCodeNameTextView);
            qrCodeLocation = itemView.findViewById(R.id.QRCodeLocationTextView);
            qrCodeWorth = itemView.findViewById(R.id.QRCodeWorthTextView);
            cardView = itemView.findViewById(R.id.cardview_in_ownerQRCode);
        }
    }
}
