package com.example.qrcodegame.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.R;
import com.example.qrcodegame.models.QRCode;

import java.util.ArrayList;

public class OwnerQRCodeAdapter extends RecyclerView.Adapter<OwnerQRCodeAdapter.viewHolder>{

    private Context context;
    private ArrayList<QRCode> qrCodes;

    public OwnerQRCodeAdapter(ArrayList<QRCode> qrCodes, Context context) {
        this.qrCodes = qrCodes;
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView qrCodeId;
        TextView qrCodeLocation;
        TextView qrCodeWorth;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeId = itemView.findViewById(R.id.QRCodeNameTextView);
            qrCodeLocation = itemView.findViewById(R.id.QRCodeLocationTextView);
            qrCodeWorth = itemView.findViewById(R.id.QRCodeWorthTextView);
        }
    }
}
