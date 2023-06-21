package com.example.super_cep.view.fragments.Chauffages;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderChauffageBinding;
import com.example.super_cep.model.Chauffage;

import java.util.Arrays;

interface ChauffageViewHolderListener {
    void onClick(Chauffage chauffage);
}
public class ChauffageViewHolder extends RecyclerView.ViewHolder {

    ViewholderChauffageBinding binding;
    public ChauffageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderChauffageBinding.bind(itemView);
    }

    public void bind(Chauffage chauffage, ChauffageViewHolderListener listener) {
        if(chauffage.uriImages.size() > 0) {
            binding.imageViewChauffage.setImageURI(chauffage.uriImages.get(0));
        }
        else {
            binding.getRoot().removeView(binding.imageViewChauffage);
        }
        binding.textViewNomChauffage.setText(chauffage.nom);
        binding.textViewTypeChauffage.setText("type : " + chauffage.type);
        binding.textViewPuissance.setText(String.valueOf("puissance : " + chauffage.puissance));
        binding.textViewQuantite.setText("quantité : " + String.valueOf(chauffage.quantite));
        binding.textViewMarque.setText("marque : " + chauffage.marque);
        binding.textViewModele.setText("modèle : " + chauffage.modele);
        StringBuilder builderZones = new StringBuilder("zones : ");
        for (String zone: chauffage.zones) {
            builderZones.append(zone).append(", ");
        }
        binding.textViewZones.setText(builderZones.toString());
        binding.textViewRegulation.setText("régulations : " + chauffage.regulation);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(chauffage);
            }
        });
    }
}