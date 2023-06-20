package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.super_cep.R;
import com.example.super_cep.databinding.PopupCalendrierModificationBinding;
import com.example.super_cep.model.Calendrier.Calendrier;
import com.example.super_cep.model.Enveloppe.Zone;

import java.util.ArrayList;
import java.util.List;


interface PopUpModificationCalendrierListener {
    void onValider(String oldName, Calendrier calendrier);
    void onSupprimer(String nomCalendrier);
}

public class PopUpModificationCalendrier extends View {

    public static void create(Context context, Zone[] zones, Calendrier calendrier, PopUpModificationCalendrierListener listener) {
        new PopUpModificationCalendrier(context,zones, calendrier, listener);
    }

    private Zone[] zones;
    private Calendrier calendrier;
    private PopupCalendrierModificationBinding binding;
    private PopUpModificationCalendrierListener listener;

    private PopupWindow pw;

    private String oldName;

    private PopUpModificationCalendrier(Context context, Zone[] zones, Calendrier calendrier, PopUpModificationCalendrierListener listener ) {
        super(context);
        this.zones = zones;
        this.calendrier = calendrier;
        this.listener = listener;
        this.oldName = calendrier.nom;
        binding = PopupCalendrierModificationBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        binding.textViewTitleCalendrier.setText(oldName);
        binding.editTextNomCalendrier.setText(oldName);
        binding.buttonSupprimer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onSupprimer(calendrier.nom);
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
                calendrier.zones = zones;
                calendrier.nom = binding.editTextNomCalendrier.getText().toString();
                listener.onValider(oldName, calendrier);
                pw.dismiss();
            }
        });

        binding.buttonAnnuler.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        setupZones();

        pw = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(this, Gravity.CENTER, 0, 0);

    }



    private void setupZones() {
        for (Zone zone: zones) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(zone.nom);
            if (containZone(zone)) {
                checkBox.setChecked(true);
            }
            binding.linearLayoutZones.addView(checkBox);
        }
    }

    private boolean containZone(Zone zone) {
        for (String calendrierZone : calendrier.zones) {
            if (calendrierZone.equals(zone.nom)) {
                return true;
            }
        }
        return false;
    }
}
