package com.example.super_cep.view.fragments.Enveloppe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Zone;
import com.example.super_cep.model.ZoneElement;

import java.util.List;

public class ZonesAdaptater extends RecyclerView.Adapter<ZoneViewHolder> {

    private List<Zone> zones;
    public ZonesAdaptater(List<Zone> zones) {
        this.zones = zones;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View zoneViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_zone, parent, false);
        return new ZoneViewHolder(zoneViewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        holder.getZoneName().setText(zones.get(position).nom);
        int nbElementPerRow = 0;
        Context context = holder.getTableLayout().getContext();
        TableLayout tableLayout = holder.getTableLayout();
        TableRow tableRow = new TableRow(context);
        tableLayout.addView(tableRow);
        List<ZoneElement> zoneElements = zones.get(position).zoneElements;
        for (ZoneElement zoneElement : zones.get(position).zoneElements) {
            if (nbElementPerRow == 4) {
                nbElementPerRow = 0;
                tableRow = new TableRow(context);
                tableLayout.addView(tableRow);
            }
            nbElementPerRow++;
            tableRow.addView(new ZoneElementView(tableRow, zoneElement));
        }
    }

    @Override
    public int getItemCount() {
        return zones.size();
    }
}
