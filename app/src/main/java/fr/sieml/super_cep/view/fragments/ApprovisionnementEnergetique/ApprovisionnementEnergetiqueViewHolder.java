package fr.sieml.super_cep.view.fragments.ApprovisionnementEnergetique;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.databinding.ViewholderApprovisionnementEnergetiqueBinding;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueElectrique;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetiqueGaz;

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
        }

        if(approviosionnementEnergetique instanceof ApprovisionnementEnergetiqueGaz){
            ApprovisionnementEnergetiqueGaz approvisionnementEnergetiqueGaz = (ApprovisionnementEnergetiqueGaz) approviosionnementEnergetique;
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
