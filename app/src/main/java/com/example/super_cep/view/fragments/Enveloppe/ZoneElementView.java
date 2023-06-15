package com.example.super_cep.view.fragments.Enveloppe;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.super_cep.databinding.ViewZoneElementBinding;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;

public class ZoneElementView extends View {

    ViewZoneElementBinding binding;
    public ZoneElementView(TableRow tableRow, Zone zone, ZoneElement zoneElement, ZoneUiHandler zoneUiHandler) {
        super(tableRow.getContext());
        binding = ViewZoneElementBinding.inflate(LayoutInflater.from(tableRow.getContext()), tableRow, true);

        TextView textViewZoneElement = binding.textViewZoneElement;
        textViewZoneElement.setText(zoneElement.getNom());

        ImageView imageView = binding.imageViewZoneElement;
        ImageFromZoneElement imageFromZoneElement = new ImageFromZoneElement();
        imageView.setImageResource(imageFromZoneElement.getImage(zoneElement));

        binding.layoutZoneElement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoneUiHandler.voirZoneElement(zoneElement);
            }
        });

        binding.layoutZoneElement.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //start drag
                ClipData.Item item = new ClipData.Item(zoneElement.getNom());
                ClipData dragData = new ClipData(zoneElement.getNom(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                ClipData.Item item2 = new ClipData.Item(zone.nom);
                dragData.addItem(item2);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(binding.layoutZoneElement);
                v.startDragAndDrop(dragData, myShadow, null, 0);

                return true;
            }
        });
    }
}
