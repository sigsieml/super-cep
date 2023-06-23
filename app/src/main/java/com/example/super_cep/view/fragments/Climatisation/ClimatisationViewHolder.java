package com.example.super_cep.view.fragments.Climatisation;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderClimatisationBinding;
import com.example.super_cep.model.Climatisation;


interface ClimatisationViewHolderListener {
    void onClick(Climatisation climatisation);
}
public class ClimatisationViewHolder extends RecyclerView.ViewHolder {

    private ViewholderClimatisationBinding binding;
    public ClimatisationViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderClimatisationBinding.bind(itemView);
    }

    public void bind(Climatisation climatisation, ClimatisationViewHolderListener listener) {
        if(climatisation.images.size() > 0) {
            binding.imageViewClimatisation.setImageURI(Uri.parse(climatisation.images.get(0)));
        }
        else {
            binding.getRoot().removeView(binding.imageViewClimatisation);
        }
        binding.textViewNomClimatisation.setText(climatisation.nom);
        binding.textViewTypeClimatisation.setText("type : " + climatisation.type);
        binding.textViewPuissance.setText(String.valueOf("puissance : " + climatisation.puissance));
        binding.textViewQuantite.setText("quantité : " + String.valueOf(climatisation.quantite));
        binding.textViewMarque.setText("marque : " + climatisation.marque);
        binding.textViewModele.setText("modèle : " + climatisation.modele);
        StringBuilder builderZones = new StringBuilder("zones : ");
        for (String zone: climatisation.zones) {
            builderZones.append(zone).append(", ");
        }
        binding.textViewZones.setText(builderZones.toString());
        binding.textViewRegulation.setText("régulations : " + climatisation.regulation);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(climatisation);
            }
        });

    }
}
