package com.example.super_cep.view.fragments.Enveloppe;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ZonesAdaptater extends RecyclerView.Adapter<ZoneViewHolder> implements Filterable {

    private Zone[] zones;
    private Zone[] zonesFiltrer;
    private ZoneUiHandler zoneUiHandler;
    public ZonesAdaptater(Zone[] zones, ZoneUiHandler zoneUiHandler) {
        this.zones = zones;
        this.zonesFiltrer = zones;
        this.zoneUiHandler = zoneUiHandler;
    }

    public void updateZone(Zone[] zones){
        this.zones = zones;
        this.zonesFiltrer = zones;
        notifyDataSetChanged();
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
        setupDragAndDrop(holder, zone);
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
            ZoneElementView zoneElementView = new ZoneElementView(tableRow,  zoneElement, new ZoneElementViewClickHandler() {
                @Override
                public void onClick(View v) { zoneUiHandler.voirZoneElement(zone, zoneElement);}
                @Override
                public void onLongClick(View v) {
                    startDragAndDrop(zone, zoneElement, v);
                }
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
        ajouterBouttonAjoutElement(tableRow, zone);
        if(zoneElements.length == 0)
            ajouterBouttonSuprimerZone(tableRow, zone);
    }

    private void startDragAndDrop(Zone zone, ZoneElement zoneElement, View v) {
        ClipData.Item item = new ClipData.Item(zoneElement.getNom());
        ClipData dragData = new ClipData(zoneElement.getNom(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
        ClipData.Item item2 = new ClipData.Item(zone.nom);
        dragData.addItem(item2);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
        v.startDragAndDrop(dragData, myShadow, null, 0);
    }

    private void setupDragAndDrop(ZoneViewHolder holder, Zone zone) {
        holder.itemView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.border_teal));
                            v.invalidate();
                            return true;
                        }

                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.border_green));
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.border_teal));
                        v.invalidate();

                        return true;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item itemZoneElement = event.getClipData().getItemAt(0);
                        CharSequence nomZoneElement = itemZoneElement.getText();
                        ClipData.Item itemZone = event.getClipData().getItemAt(1);
                        CharSequence nomZone = itemZone.getText();
                        Log.i("drag",  "Dragged data is " + nomZoneElement + " moved to " + zone.nom + " from " + nomZone);

                        zoneUiHandler.moveZoneElement(nomZoneElement.toString(), nomZone.toString(), zone.nom);

                        ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.border_black));
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.border_black));
                        v.invalidate();
                        return true;
                }
                return false;
            }
        });
    }

    private void ajouterBouttonAjoutElement(TableRow tableRow, Zone zone) {
        View buttonAjout = LayoutInflater.from(tableRow.getContext()).inflate(R.layout.view_zone_ajouter_un_element, tableRow, false);


        // Define LayoutParams for ZoneElementView
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        buttonAjout.setLayoutParams(lp);
        tableRow.addView(buttonAjout);
        buttonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoneUiHandler.nouvelleElementZone(zone);
            }
        });
    }

    private void ajouterBouttonSuprimerZone(TableRow tableRow, Zone zone){
        View buttonSuprimer = LayoutInflater.from(tableRow.getContext()).inflate(R.layout.view_zone_suprimer_zone, tableRow, false);
// Define LayoutParams for ZoneElementView

        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        buttonSuprimer.setLayoutParams(lp);
        tableRow.addView(buttonSuprimer);
        buttonSuprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Zone " + zone.nom + " supprimer", Snackbar.LENGTH_LONG)
                        .setAction("Oui", null).show();
                zoneUiHandler.deleteZone(zone);}
        });
    }

    @Override
    public int getItemCount() {
        return zones.length ;
    }

    @Override
    public Filter getFilter() {
        return new ItemFilter();
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
