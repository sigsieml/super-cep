package com.example.super_cep.view.fragments.Enveloppe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.super_cep.R;
import com.example.super_cep.model.ZoneElement;

public class ZoneElementView extends View {
    public ZoneElementView(TableRow tableRow, ZoneElement zoneElement) {
        super(tableRow.getContext());
        LayoutInflater inflater = LayoutInflater.from(tableRow.getContext());
        View view =  inflater.inflate(R.layout.view_zone_element, tableRow, true);
        TextView textViewZoneElement = view.findViewById(R.id.textViewZoneElement);
        textViewZoneElement.setText(zoneElement.getNom());
    }
}
