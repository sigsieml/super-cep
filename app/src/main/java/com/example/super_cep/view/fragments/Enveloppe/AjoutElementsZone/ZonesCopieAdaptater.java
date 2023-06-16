package com.example.super_cep.view.fragments.Enveloppe.AjoutElementsZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElementView;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElementViewClickHandler;
import com.example.super_cep.view.fragments.Enveloppe.ZoneViewHolder;

import java.util.ArrayList;

interface ElementZoneCopieHandler {
    void onClick(Zone zone, ZoneElement zoneElement);
}

public class ZonesCopieAdaptater extends RecyclerView.Adapter<ZoneViewHolder> implements Filterable {
    private Zone[] zones;

    private Zone[] zonesFiltrer;
    private ElementZoneCopieHandler elementZoneCopieHandler;

    public ZonesCopieAdaptater(Zone[] zones, ElementZoneCopieHandler elementZoneCopieHandler) {
        this.zones = zones;
        this.zonesFiltrer = zones;
        this.elementZoneCopieHandler = elementZoneCopieHandler;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View zoneViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_zone, parent, false);
        return new ZoneViewHolder(zoneViewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        Zone zone = zonesFiltrer[position];
        holder.getZoneName().setText(zone.nom);
        updateZoneElements(holder, zone);
    }

    private void updateZoneElements(ZoneViewHolder holder, Zone zone) {
        int nbElementPerRow = 0;
        Context context = holder.getTableLayout().getContext();
        TableLayout tableLayout = holder.getTableLayout();
        tableLayout.removeAllViews();
        TableRow tableRow = new TableRow(context);
        tableLayout.addView(tableRow);
        ZoneElement[] zoneElements = zone.getZoneElementsValues();
        for (ZoneElement zoneElement : zone.getZoneElementsValues()) {
            ZoneElementView zoneElementView = new ZoneElementView(tableRow, zoneElement, new ZoneElementViewClickHandler() {
                @Override
                public void onClick(View v) {
                    elementZoneCopieHandler.onClick(zone, zoneElement);
                }
                @Override
                public void onLongClick(View v) {}
            });

            // Define LayoutParams for ZoneElementView
            TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
            zoneElementView.setLayoutParams(lp);
            nbElementPerRow++;
            tableRow.addView(zoneElementView);

            if (nbElementPerRow == 4) {
                nbElementPerRow = 0;
                tableRow = new TableRow(context);
                tableLayout.addView(tableRow);
            }

        }
    }
    @Override
    public int getItemCount() {
        return zones.length ;
    }

    @Override
    public Filter getFilter() {
        return new ItemFilter();
    }

    public void updateZones(Zone[] zones) {
        this.zones = zones;
        this.zonesFiltrer = zones;
        notifyDataSetChanged();
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString();
            FilterResults results = new FilterResults();

            if(filterString.isEmpty()) {
                results.values = zones;
                results.count = zones.length;
                return results;
            }


            final Zone[] list = zones;

            int count = list.length;
            final Zone[] nlist = new Zone[count];

            Zone zoneToFilter;


            for (int i = 0; i < count; i++) {
                zoneToFilter = list[i];
                nlist[i] = new Zone(zoneToFilter.nom, new ArrayList<>());
                for (ZoneElement zoneElement :
                        zoneToFilter.getZoneElementsValues()) {
                    String[] zoneElementWord = zoneElement.getNom().split(" ");
                    for (String s :
                            zoneElementWord) {
                        if (isStringSimilar(filterString, s.substring(0, Math.min(s.length(), filterString.length()) ) ) ) {
                            nlist[i].addZoneElement(zoneElement);
                            break;
                        }
                    }
                }

            }

            results.values = nlist;
            results.count = nlist.length;

            return results;
        }

        private boolean isStringSimilar(String a, String b){
            return levenshteinDistance(a.toLowerCase(), b.toLowerCase()) < (a.length() / 2);
        }

        private int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
            int len0 = lhs.length() + 1;
            int len1 = rhs.length() + 1;
            int[] cost = new int[len0];
            int[] newcost = new int[len0];
            for (int i = 0; i < len0; i++) {
                cost[i] = i;
            }
            for (int j = 1; j < len1; j++) {
                newcost[0] = j;
                for (int i = 1; i < len0; i++) {
                    int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                    int costReplace = cost[i - 1] + match;
                    int costInsert = cost[i] + 1;
                    int costDelete = newcost[i - 1] + 1;
                    newcost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
                }
                int[] swap = cost;
                cost = newcost;
                newcost = swap;
            }
            return cost[len0 - 1];
        }



        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            zonesFiltrer = (Zone[]) results.values;
            notifyDataSetChanged();
        }
    }
}
