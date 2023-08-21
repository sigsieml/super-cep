package fr.sieml.super_cep.view.fragments.Enveloppe.AjoutElementsZone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.databinding.FragmentAjoutElementZoneBinding;
import fr.sieml.super_cep.model.Releve.Zone;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.controller.ReleveViewModel;

public class AjoutElementZone extends Fragment {

    private static final String ARG_PARAM1 = "nomZone";
    private String nomZone;
    public AjoutElementZone() {}
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
    private ReleveViewModel releveViewModel;
    private LiveData<Releve> releve;
    public Zone[] zones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjoutElementZoneBinding.inflate(inflater, container, false);
        binding.textViewNomZone.setText("nouvel élément dans la zone " + nomZone);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();

        binding.buttonAnnulerAjoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        binding.layoutMur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentMur(nomZone, null, null));
            }
        });

        binding.layoutToitureEtFauxPlafond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentToitureOuFauxPlafond(nomZone, null, null));
            }
        });

        binding.layoutMenuiserie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentMenuiserie(nomZone, null, null));
            }
        });

        binding.layoutSols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentSol(nomZone, null, null));
            }
        });

        binding.layoutEclairage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OuvrirFragmentAjout(AjoutElementZoneDirections.actionNavAjoutElementZoneToFragmentEclairage(nomZone, null, null));
            }
        });

        return binding.getRoot();
    }


    private void OuvrirFragmentAjout(NavDirections navDirections){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(navDirections);
    }


    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }

}