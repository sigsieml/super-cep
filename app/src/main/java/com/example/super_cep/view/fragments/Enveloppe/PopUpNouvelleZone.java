package com.example.super_cep.view.fragments.Enveloppe;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.super_cep.R;
import com.example.super_cep.databinding.PopupNouvelleZoneBinding;

public class PopUpNouvelleZone extends View {
    PopupNouvelleZoneBinding binding;
    public PopUpNouvelleZone(Context context, Enveloppe enveloppe) {
        super(context);
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_nouvelle_zone, null);
        binding = PopupNouvelleZoneBinding.bind(popupView);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow pw = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        pw.setAnimationStyle(R.style.Animation);
        pw.update();

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
                enveloppe.nouvelleZone(binding.editTextNomNouvelleZone.getText().toString());
            }
        });
    }
}
