package com.example.super_cep.view.fragments.Chauffages;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;
import com.example.super_cep.model.Releve.Chauffage.ChauffageCentraliser;
import com.example.super_cep.model.Releve.Chauffage.ChauffageDecentraliser;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElementView;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElementViewClickHandler;
import com.example.super_cep.view.fragments.Enveloppe.ZoneUiHandler;
import com.example.super_cep.view.fragments.Enveloppe.ZoneViewHolder;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.collections4.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChauffagesAdapter extends RecyclerView.Adapter<ZoneViewHolder> {


    private Map<String, List<Chauffage>> zonesChauffage;

    private ZoneChauffageHandler listener;
    public ChauffagesAdapter(LifecycleOwner lifecycleOwner, ReleveViewModel releveViewModel, ZoneChauffageHandler listener) {
        this.listener = listener;
        zonesChauffage = new HashMap<>();
        zonesChauffage = setZonesChauffagesFromChauffages(releveViewModel.getReleve().getValue());
        releveViewModel.getReleve().observe(lifecycleOwner, releve -> update(releve));
    }



    public void update(Releve releve) {
        zonesChauffage = setZonesChauffagesFromChauffages(releve);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_zone, parent, false);
        return new ZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        TextView zoneName = holder.getZoneName();
        Map.Entry<String, List<Chauffage>> keyValue = (Map.Entry<String, List<Chauffage>>) zonesChauffage.entrySet().toArray()[position];
        zoneName.setText(keyValue.getKey());
        setupChauffagesInZone(holder, keyValue.getKey(), keyValue.getValue());
        setupDragAndDrop(holder, keyValue.getKey());



    }

    private void setupChauffagesInZone(ZoneViewHolder holder, String nomZone, List<Chauffage> chauffages){
        TableLayout tableLayout = holder.getTableLayout();
        Context context = holder.getTableLayout().getContext();
        int nbElementPerRow = 0;
        tableLayout.removeAllViews();
        TableRow tableRow = new TableRow(context);
        tableLayout.addView(tableRow);

        for (Chauffage chauffage : chauffages) {
            String logoChauffage = chauffage instanceof ChauffageCentraliser ? "centraliser" : "decentraliser";
            ZoneElementView zoneElementView = new ZoneElementView(tableRow, chauffage.nom, logoChauffage, new ZoneElementViewClickHandler() {
                @Override
                public void onClick(View v) { listener.voirZoneElement(nomZone, chauffage);}
                @Override
                public void onLongClick(View v) {
                    startDragAndDrop(nomZone, chauffage, v);
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
        ajouterBouttonAjoutElement(tableRow, nomZone);
        if(chauffages.size() == 0)
            ajouterBouttonSuprimerZone(tableRow, nomZone);
    }

    private void ajouterBouttonAjoutElement(TableRow tableRow, String nomZone) {
        View buttonAjout = LayoutInflater.from(tableRow.getContext()).inflate(R.layout.view_zone_ajouter_un_element, tableRow, false);
        // Define LayoutParams for ZoneElementView
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        buttonAjout.setLayoutParams(lp);
        tableRow.addView(buttonAjout);
        buttonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.nouvelleElementZone(nomZone);
            }
        });
    }

    private void ajouterBouttonSuprimerZone(TableRow tableRow, String nomZone){
        View buttonSuprimer = LayoutInflater.from(tableRow.getContext()).inflate(R.layout.view_zone_suprimer_zone, tableRow, false);
// Define LayoutParams for ZoneElementView

        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        buttonSuprimer.setLayoutParams(lp);
        tableRow.addView(buttonSuprimer);
        buttonSuprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Voulez vous supprimer la zone " + nomZone + " ?" + "\n" + "Tous les éléments associés à la zone seront supprimés")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                listener.deleteZone(nomZone);
                                Toast.makeText(v.getContext(), "Zone " + nomZone + " supprimer", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });
    }


    private void startDragAndDrop(String nomZone, Chauffage chauffage, View v) {
        ClipData.Item item = new ClipData.Item(chauffage.nom);
        ClipData dragData = new ClipData(chauffage.nom, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
        ClipData.Item item2 = new ClipData.Item(nomZone);
        dragData.addItem(item2);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
        v.startDragAndDrop(dragData, myShadow, null, 0);
    }

    private void setupDragAndDrop(ZoneViewHolder holder, String nomZoneOrigine) {
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
                        Log.i("drag",  "Dragged data is " + nomZoneElement + " moved to " + nomZoneOrigine + " from " + nomZone);

                        listener.moveZoneElement(nomZoneElement.toString(), nomZone.toString(), nomZoneOrigine);

                        ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.white_round));
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        ((ConstraintLayout) v).setBackground(v.getContext().getDrawable(R.drawable.white_round));
                        v.invalidate();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return zonesChauffage.values().size();
    }

    private Map<String, List<Chauffage>> setZonesChauffagesFromChauffages(Releve releve) {
        Chauffage[] chauffages = releve.chauffages.values().toArray(new Chauffage[0]);
        Map<String, List<Chauffage>> zonesChauffage = new HashMap<>();
        for(Zone zone : releve.zones.values()){
            zonesChauffage.put(zone.nom, new ArrayList<>());
        }
        for(Chauffage chauffage : chauffages){
            if(chauffage instanceof ChauffageCentraliser){
                ChauffageCentraliser chauffageCentraliser = (ChauffageCentraliser) chauffage;
                for(String zoneNom : chauffageCentraliser.zones){
                    if(zonesChauffage.containsKey(zoneNom)){
                        zonesChauffage.get(zoneNom).add(chauffageCentraliser);
                    }
                }
            }else if(chauffage instanceof ChauffageDecentraliser){
                ChauffageDecentraliser chauffageDecentraliser = (ChauffageDecentraliser) chauffage;
                if(zonesChauffage.containsKey(chauffageDecentraliser.zone)) {
                    zonesChauffage.get(chauffageDecentraliser.zone).add(chauffageDecentraliser);
                }
            }

        }
        return zonesChauffage;
    }
}
