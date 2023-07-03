package com.example.super_cep.view.fragments.Preconisation;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.ConfigDataViewModel;
import com.example.super_cep.databinding.PopupPreconisationsBinding;

import java.util.ArrayList;
import java.util.List;


interface PopUpNouvellePreconisationListener{
    void onValidate(List<String> preconisation);
}
public class PopUpNouvellePreconisation extends View {

    public static void create(Context context, ReleveViewModel releveViewModel, ConfigDataViewModel configDataViewModel, PopUpNouvellePreconisationListener popupNouvelleRemarqueListener) {
        new PopUpNouvellePreconisation(context, releveViewModel, configDataViewModel, popupNouvelleRemarqueListener);
    }

    private PopupPreconisationsBinding binding;
    private PopUpNouvellePreconisationListener listener;
    private PopupWindow pw;
    private ConfigDataViewModel configDataViewModel;
    private ReleveViewModel releveViewModel;
    private List<CheckBox> checkBoxesPreconisations = new ArrayList<>();

    public PopUpNouvellePreconisation(Context context, ReleveViewModel releveViewModel, ConfigDataViewModel configDataViewModel, PopUpNouvellePreconisationListener listener) {
        super(context);
        this.releveViewModel = releveViewModel;
        this.configDataViewModel = configDataViewModel;
        this.listener = listener;
        this.binding = PopupPreconisationsBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setupSpinner();
        pw = new PopupWindow(binding.getRoot(), ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        binding.buttonAnnuler.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        binding.buttonPreconisationPersonalise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onValidate(List.of(""));
                pw.dismiss();
            }
        });

        binding.buttonValider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> nouvellePreconisations = new ArrayList<>();
                for (CheckBox checkBox : checkBoxesPreconisations) {
                    if(checkBox.isChecked()){
                        nouvellePreconisations.add(checkBox.getText().toString());
                    }
                }
                listener.onValidate(nouvellePreconisations);
                pw.dismiss();
            }
        });
    }


    private void setupSpinner() {
        List<String> preconisationsNom = releveViewModel.getReleve().getValue().preconisations;
        List<String> spinnerDataRemarque = configDataViewModel.getSpinnerData().getValue().get("preconisations");
        for (String s :spinnerDataRemarque) {
            if(!preconisationsNom.contains(s)){
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setText(s);
                checkBoxesPreconisations.add(checkBox);
                binding.linearLayoutPreconisations.addView(checkBox);
            }
        }
    }
}
