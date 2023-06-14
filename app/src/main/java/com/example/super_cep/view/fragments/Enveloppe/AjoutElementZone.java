package com.example.super_cep.view.fragments.Enveloppe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.databinding.FragmentAjoutElementZoneBinding;
import com.example.super_cep.view.fragments.Enveloppe.ZoneElementConsultation.AjoutMur;

public class AjoutElementZone extends Fragment {

    private static final String ARG_PARAM1 = "nomZone";
    private String nomZone;

    public AjoutElementZone() {
        // Required empty public constructor
    }

    public static AjoutElementZone newInstance(String nomZone) {
        AjoutElementZone fragment = new AjoutElementZone();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, nomZone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(ARG_PARAM1);
    }

    FragmentAjoutElementZoneBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjoutElementZoneBinding.inflate(inflater, container, false);
        binding.textViewNomZone.setText("nouvel élément dans la zone " + nomZone);
        binding.buttonAnnulerAjoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.layoutMur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutMur.newInstance(nomZone));
            }
        });

        return binding.getRoot();
    }


    private void OuvrirFragmentAjout(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(((View)binding.getRoot().getParent()).getId(), fragment, fragment.toString());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }
}