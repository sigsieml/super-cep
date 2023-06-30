package com.example.super_cep.view.fragments.Enveloppe;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.PopupNouvelleZoneBinding;
import com.example.super_cep.model.Releve.Releve;

public class PopUpZone extends View {

    public static void show(Context context, PopUpZoneHandler handler, ReleveViewModel releveViewModel){
        new PopUpZone(context, handler, releveViewModel);
    }

    public static void show(Context context, PopUpZoneHandler handler, ReleveViewModel releveViewModel, String zoneModifier){
        new PopUpZone(context, handler, releveViewModel, zoneModifier);
    }

    PopupNouvelleZoneBinding binding;
    private ReleveViewModel releveViewModel;
    private PopUpZoneHandler handler;
    private final PopupWindow pw;

    private String zoneModifier;

    private PopUpZone(Context context, PopUpZoneHandler handler, ReleveViewModel releveViewModel) {
        this(context, handler, releveViewModel, null);
        setupNomZone();
    }


    private PopUpZone(Context context, PopUpZoneHandler handler, ReleveViewModel releveViewModel, String zoneModifier){
        super(context);
        this.releveViewModel = releveViewModel;
        this.handler = handler;
        this.zoneModifier = zoneModifier;
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_nouvelle_zone, null);
        binding = PopupNouvelleZoneBinding.bind(popupView);
        pw = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        setupButtons();

        if(zoneModifier != null){
            binding.editTextNomNouvelleZone.setText(zoneModifier);
            binding.textViewTitle.setText("Modifier le nom de la  zone : " + zoneModifier);
        }

    }

    private void setupButtons() {
        binding.buttonAnnuler.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        binding.buttonValider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                if(zoneModifier != null){
                    handler.editZone(zoneModifier, binding.editTextNomNouvelleZone.getText().toString());
                }
                else{
                    handler.nouvelleZone(binding.editTextNomNouvelleZone.getText().toString());
                }
            }
        });
    }

    private void setupNomZone() {
        int index = 1;
        String element  = "Zone ";
        Releve releve = releveViewModel.getReleve().getValue();
        while(releve.zones.containsKey(element + index)){
            index++;
        }
        binding.editTextNomNouvelleZone.setText(element + index);
    }
}
