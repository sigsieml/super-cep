package com.example.super_cep.view.fragments.Enveloppe;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.super_cep.databinding.ViewZoneElementBinding;
import com.example.super_cep.model.Releve.ZoneElement;

public class ZoneElementView extends View {

    ViewZoneElementBinding binding;
    public ZoneElementView(TableRow tableRow,String nom,  String logo, ZoneElementViewClickHandler zoneUiHandler) {
        super(tableRow.getContext());
        binding = ViewZoneElementBinding.inflate(LayoutInflater.from(tableRow.getContext()), tableRow, true);

        TextView textViewZoneElement = binding.textViewZoneElement;
        textViewZoneElement.setText(nom);

        ImageView imageView = binding.imageViewZoneElement;
        ImageFromZoneElement imageFromZoneElement = new ImageFromZoneElement();
        imageView.setImageResource(imageFromZoneElement.getImage(logo));

        binding.layoutZoneElement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoneUiHandler.onClick(v);
            }
        });

        binding.layoutZoneElement.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                zoneUiHandler.onLongClick(v);
                return true;
            }
        });
    }
}
