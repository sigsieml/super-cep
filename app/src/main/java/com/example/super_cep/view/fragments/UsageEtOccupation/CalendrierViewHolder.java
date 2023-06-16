package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderCalendrierBinding;
import com.example.super_cep.model.Calendrier.Calendrier;
import com.example.super_cep.model.Enveloppe.Zone;

public class CalendrierViewHolder extends RecyclerView.ViewHolder {


    public ViewholderCalendrierBinding binding;
    public CalendrierViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderCalendrierBinding.bind(itemView);
    }

    public void bind(Calendrier calendrier, Zone[] zones, PopUpModificationCalendrierListener listener) {
        binding.textViewtitleCalendrier.setText(calendrier.nom);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpModificationCalendrier.create(itemView.getContext(), zones, calendrier, listener);
            }
        });

        StringBuilder stringBuilder = new StringBuilder("zones : ");
        for (Zone zone : calendrier.zones) {
            stringBuilder.append(zone.nom);
            stringBuilder.append(", ");
        }
        binding.textViewZones.setText(stringBuilder.toString());
    }

}
