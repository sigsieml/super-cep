package com.example.super_cep.view.fragments.Remarques;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.PopupNouvelleRemarqueBinding;
import com.example.super_cep.model.Remarque;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


interface PopupNouvelleRemarqueListener{
    void onValidate(Remarque remarque);
}
public class PopupNouvelleRemarque extends View {

    public static final String NOM_REMARQUE_PERSONNALISEE = "Remarque personnalisee";


    public static void create(Context context, ReleveViewModel releveViewModel, SpinnerDataViewModel spinnerDataViewModel, PopupNouvelleRemarqueListener popupNouvelleRemarqueListener) {
        new PopupNouvelleRemarque(context, releveViewModel, spinnerDataViewModel, popupNouvelleRemarqueListener);
    }

    private PopupNouvelleRemarqueBinding binding;
    private PopupNouvelleRemarqueListener listener;
    private PopupWindow pw;
    private SpinnerDataViewModel spinnerDataViewModel;
    private ReleveViewModel releveViewModel;

    public PopupNouvelleRemarque(Context context, ReleveViewModel releveViewModel, SpinnerDataViewModel spinnerDataViewModel, PopupNouvelleRemarqueListener listener) {
        super(context);
        this.releveViewModel = releveViewModel;
        this.spinnerDataViewModel = spinnerDataViewModel;
        this.listener = listener;
        this.binding = PopupNouvelleRemarqueBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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

        binding.buttonValider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onValidate(new Remarque(binding.spinnerNomRemarque.getSelectedItem().toString(), "", isRemarquePersonnaliseeSelected()));
                pw.dismiss();
            }
        });
    }

    private boolean isRemarquePersonnaliseeSelected() {
        return binding.spinnerNomRemarque.getSelectedItem().toString().equals(NOM_REMARQUE_PERSONNALISEE);
    }

    private void setupSpinner() {
        Set<String> remarquesNom = releveViewModel.getReleve().getValue().remarques.keySet();
        List<String> spinnerDataRemarque = spinnerDataViewModel.getSpinnerData().getValue().get("nomRemarques");
        List<String> customRemarques = new ArrayList<>();
        for (String s :spinnerDataRemarque) {
            if(!remarquesNom.contains(s)){
                customRemarques.add(s);
            }
        }
        customRemarques.add(NOM_REMARQUE_PERSONNALISEE);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerNomRemarque, customRemarques);

    }

}
