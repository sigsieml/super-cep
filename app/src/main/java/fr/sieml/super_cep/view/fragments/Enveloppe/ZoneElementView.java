package fr.sieml.super_cep.view.fragments.Enveloppe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.sieml.super_cep.databinding.ViewZoneElementBinding;

public class ZoneElementView extends View {

    ViewZoneElementBinding binding;
    public ZoneElementView(ViewGroup container, String nom, String logo, ZoneElementViewClickHandler zoneUiHandler) {
        super(container.getContext());
        binding = ViewZoneElementBinding.inflate(LayoutInflater.from(container.getContext()), container, true);

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
