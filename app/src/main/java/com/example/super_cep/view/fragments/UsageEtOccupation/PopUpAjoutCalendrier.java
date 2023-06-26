package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.super_cep.R;
import com.example.super_cep.databinding.PopupCalendrierAjoutBinding;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Enveloppe.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


interface PopUpAjoutCalendrierListener {
    void onValider(Calendrier calendrier);
}
public class PopUpAjoutCalendrier extends View {

    public static void create(Context context, Zone[] zones, PopUpAjoutCalendrierListener listener) {
        new PopUpAjoutCalendrier(context,zones, listener);
    }
    private PopupWindow pw;
    private PopupCalendrierAjoutBinding binding;
    private PopUpAjoutCalendrierListener listener;

    private Zone[] zones;

    private PopUpAjoutCalendrier(Context context,Zone[] zones, PopUpAjoutCalendrierListener listener) {
        super(context);
        this.listener = listener;
        this.zones = zones;
        binding = PopupCalendrierAjoutBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        setupZones();

        binding.buttonAnnuler.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        binding.buttonValider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> zones = new ArrayList<>();
                for (View view  : binding.linearLayoutZones.getTouchables()) {
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        if (checkBox.isChecked()) {
                            zones.add(checkBox.getText().toString());
                        }
                    }
                }
                listener.onValider(new Calendrier(binding.editTextNomCalendrier.getText().toString(), zones, new HashMap<>()));
                pw.dismiss();
            }
        });

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        //get width and height from binding.getRoot()
        pw = new PopupWindow(binding.getRoot(), width,height, true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);


    }

    public Zone getZoneByName(String name){
        for (Zone zone : zones) {
            if (zone.nom.equals(name)) {
                return zone;
            }
        }
        throw new RuntimeException("Zone not found");
    }

    private void setupZones() {
        for (Zone zone: zones) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(zone.nom);
            binding.linearLayoutZones.addView(checkBox);
        }
    }

}
