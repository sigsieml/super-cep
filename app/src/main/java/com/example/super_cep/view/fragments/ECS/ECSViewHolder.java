package com.example.super_cep.view.fragments.ECS;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderEcsBinding;
import com.example.super_cep.model.Releve.ECS;


interface ECSViewHolderListener {
    void onClick(ECS ecs);
}
public class ECSViewHolder extends RecyclerView.ViewHolder {

    private ViewholderEcsBinding binding;
    public ECSViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderEcsBinding.bind(itemView);
    }

    public void bind(ECS ecs, ECSViewHolderListener listener) {
        if(ecs.images.size() > 0) {
            binding.imageViewECS.setImageURI(Uri.parse(ecs.images.get(0)));
        }
        else {
            binding.getRoot().removeView(binding.imageViewECS);
        }
        binding.textViewNomECS.setText(ecs.nom);
        binding.textViewTypeECS.setText("type : " + ecs.type);
        binding.textViewVolume.setText("volume : " + ecs.volume + " m3");
        binding.textViewQuantite.setText("quantité : " + ecs.quantite);
        binding.textViewMarque.setText("marque : " + ecs.marque);
        binding.textViewModele.setText("modèle : " + ecs.modele);

        StringBuilder builderZones = new StringBuilder("zones : ");
        for (String zone: ecs.zones) {
            builderZones.append(zone).append(", ");
        }
        binding.textViewZones.setText(builderZones.toString());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(ecs);
            }
        });

    }
}
