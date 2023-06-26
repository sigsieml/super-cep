package com.example.super_cep.view.fragments.ApprovisionnementEnergetique;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderApprovisionnementEnergetiqueBinding;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import com.example.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;

interface ApprovisionnementEnergetiqueViewHolderListener{
    void onClick(ApprovisionnementEnergetique approvisionnementEnergetique);
}

public class ApprovisionnementEnergetiqueViewHolder extends RecyclerView.ViewHolder {

    private ViewholderApprovisionnementEnergetiqueBinding binding;
    public ApprovisionnementEnergetiqueViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderApprovisionnementEnergetiqueBinding.bind(itemView);
    }

    public void bind(ApprovisionnementEnergetique approviosionnementEnergetique, ApprovisionnementEnergetiqueViewHolderListener listener) {
        if(approviosionnementEnergetique.images.size() > 0) {
            binding.imageViewApprovisionnementEnergetique.setImageURI(Uri.parse(approviosionnementEnergetique.images.get(0)));
        }
        else {
            binding.getRoot().removeView(binding.imageViewApprovisionnementEnergetique);
        }
        binding.textViewNomApprovisionnementEnergetique.setText(approviosionnementEnergetique.nom);
        binding.textViewEnergie.setText(approviosionnementEnergetique.energie);
        binding.linearLayout.removeAllViews();
        if(approviosionnementEnergetique instanceof ApprovisionnementEnergetiqueElectrique){
            ApprovisionnementEnergetiqueElectrique approvisionnementEnergetiqueElectrique = (ApprovisionnementEnergetiqueElectrique) approviosionnementEnergetique;
            TextView textViewPuissance = new TextView(binding.getRoot().getContext());
            textViewPuissance.setText("Puissance : " + approvisionnementEnergetiqueElectrique.puissance + " kW");
            binding.linearLayout.addView(textViewPuissance);
            TextView textViewFormule = new TextView(binding.getRoot().getContext());
            textViewFormule.setText("Formule : " + approvisionnementEnergetiqueElectrique.formuleTarifaire);
            binding.linearLayout.addView(textViewFormule);
            TextView textViewNumeroPDL = new TextView(binding.getRoot().getContext());
            textViewNumeroPDL.setText("Numéro PDL : " + approvisionnementEnergetiqueElectrique.numeroPDL);
            binding.linearLayout.addView(textViewNumeroPDL);
        }

        if(approviosionnementEnergetique instanceof ApprovisionnementEnergetiqueGaz){
            ApprovisionnementEnergetiqueGaz approvisionnementEnergetiqueGaz = (ApprovisionnementEnergetiqueGaz) approviosionnementEnergetique;
            TextView textViewNumeroRAE = new TextView(binding.getRoot().getContext());
            textViewNumeroRAE.setText("Numéro RAE : " + approvisionnementEnergetiqueGaz.numeroRAE);
            binding.linearLayout.addView(textViewNumeroRAE);
        }



        StringBuilder builderZones = new StringBuilder("zones : ");
        for (String zone: approviosionnementEnergetique.zones) {
            builderZones.append(zone).append(", ");
        }
        binding.textViewZones.setText(builderZones.toString());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(approviosionnementEnergetique);
            }
        });

    }
}
