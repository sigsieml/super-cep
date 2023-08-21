package fr.sieml.super_cep.view.fragments.Ventilation;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.databinding.ViewholderVentilationBinding;
import fr.sieml.super_cep.model.Releve.Ventilation;


interface VentilationViewHolderListener {
    void onClick(Ventilation ventilation);
}
public class VentilationViewHolder extends RecyclerView.ViewHolder {

    private ViewholderVentilationBinding binding;
    public VentilationViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderVentilationBinding.bind(itemView);
    }

    public void bind(Ventilation ventilation, VentilationViewHolderListener listener) {
        if(ventilation.images.size() > 0) {
            binding.imageViewVentilation.setImageURI(Uri.parse(ventilation.images.get(0)));
        }
        else {
            binding.getRoot().removeView(binding.imageViewVentilation);
        }
        binding.textViewNomVentilation.setText(ventilation.nom);
        binding.textViewTypeVentilation.setText("type : " + ventilation.type);
        StringBuilder builderZones = new StringBuilder("zones : ");
        for (String zone: ventilation.zones) {
            builderZones.append(zone).append(", ");
        }
        binding.textViewZones.setText(builderZones.toString());
        binding.textViewRegulation.setText("régulations : " + ventilation.regulation);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(ventilation);
            }
        });

    }
}
