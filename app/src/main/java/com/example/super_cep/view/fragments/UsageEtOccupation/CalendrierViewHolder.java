package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.controller.Animation;
import com.example.super_cep.databinding.ViewholderCalendrierBinding;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Zone;


interface CalendrierViewHolderListener{
    void onClick(Calendrier calendrier);
    void onLongClick(Calendrier calendrier);
}

public class CalendrierViewHolder extends RecyclerView.ViewHolder {


    public ViewholderCalendrierBinding binding;
    public CalendrierViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderCalendrierBinding.bind(itemView);
        Animation.pulseViewOnLongPress(binding.getRoot());
    }

    public void bind(Calendrier calendrier, Zone[] zones, CalendrierViewHolderListener listener){
        binding.textViewtitleCalendrier.setText(calendrier.nom);

        binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(calendrier);
                return true;
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(calendrier);
            }
        });

        StringBuilder stringBuilder = new StringBuilder("zones : ");

        for (String zone : calendrier.zones) {
            //check if zone is in zones
            stringBuilder.append(zone);
            stringBuilder.append(", ");
        }
        binding.textViewZones.setText(stringBuilder.toString());
    }

}
