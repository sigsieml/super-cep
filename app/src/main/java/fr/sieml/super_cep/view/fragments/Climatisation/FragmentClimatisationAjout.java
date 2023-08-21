package fr.sieml.super_cep.view.fragments.Climatisation;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.controller.ConfigDataViewModel;
import fr.sieml.super_cep.databinding.FragmentClimatisationAjoutBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import fr.sieml.super_cep.model.Releve.Climatisation;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.view.Mode;
import fr.sieml.super_cep.view.includeView.ViewPhoto;
import fr.sieml.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;

public class FragmentClimatisationAjout extends Fragment {


    private static final String ARG_NOM_CLIMATISATION = "nomClimatisation";

    private String nomClimatisation;


    private Mode mode = Mode.Ajout;
    public FragmentClimatisationAjout() {
        // Required empty public constructor
    }

    public static FragmentClimatisationAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentClimatisationAjout newInstance(String nomClimatisation) {
        FragmentClimatisationAjout fragment = new FragmentClimatisationAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_CLIMATISATION, nomClimatisation);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_CLIMATISATION) != null){
            mode = Mode.Edition;
            nomClimatisation = getArguments().getString(ARG_NOM_CLIMATISATION);
        }

    }

    private FragmentClimatisationAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;

    private ViewPhoto viewPhoto;
    private ViewZoneSelector viewZoneSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClimatisationAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);

        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);
        updateSpinner();

        if(mode == Mode.Ajout){
            prefillClimatisationName();
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                Climatisation climatisation = releveViewModel.getReleve().getValue().climatisations.get(nomClimatisation);
                setModeEdition(climatisation);
                addDataToView(climatisation);
            }
        }catch (Exception e){
            Log.e("Ajout climatisation", "onCreateView: ", e);
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            back();
        }

        return binding.getRoot();

    }

    private void addFooterAjout() {
        ViewFooterZoneElementBinding viewFooter = ViewFooterZoneElementBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClimatisationToReleve();
            }
        });

        binding.linearLayoutAjoutClimatisation.addView(viewFooter.getRoot());
    }

    private void setModeEdition(Climatisation climatisation) {
        binding.textViewTitleClimatisation.setText(climatisation.nom);
        ViewFooterZoneElementConsultationBinding viewFooter = ViewFooterZoneElementConsultationBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClimatisation();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeClimatisation(nomClimatisation);
                back();
            }
        });

        binding.linearLayoutAjoutClimatisation.addView(viewFooter.getRoot());
    }






    private void addClimatisationToReleve() {
        try {
            releveViewModel.addClimatisation(getClimatisationFromViews());
            back();

        }catch (Exception e){
            Log.e("Climatisation", "addClimatisationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editClimatisation(){
        try {
            releveViewModel.editClimatisation(nomClimatisation, getClimatisationFromViews());
            back();
        } catch (Exception e) {
            Log.e("Climatisation", "addClimatisationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    private void updateSpinner() {
        configDataViewModel.setAutoComplete(binding.autoCompleteRegulations, "regulationClimatisation");
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeClimatisation, "typeClimatisation");
        configDataViewModel.setAutoComplete(binding.autoCompleteMarque, "marqueClimatisation");
    }

    private Climatisation getClimatisationFromViews(){
        List<String> images = new ArrayList<>();
        for(Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        Climatisation climatisation = new Climatisation(
                binding.editTextNomClimatisation.getText().toString(),
                binding.autoCompleteTypeClimatisation.getText().toString(),
                binding.editTextNumberPuissance.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextNumberPuissance.getText().toString()),
                binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                binding.autoCompleteMarque.getText().toString(),
                binding.editTextModele.getText().toString(),
                binding.autoCompleteRegulations.getText().toString(),
                viewZoneSelector.getSelectedZones(),
                images,
                binding.checkBoxAVerifierClimatisation.isChecked(),
                binding.editTextMultilineNoteClimatisation.getText().toString()
        );

        return climatisation;
    }

    private void addDataToView(Climatisation climatisation){
        binding.editTextNomClimatisation.setText(climatisation.nom);
        binding.autoCompleteTypeClimatisation.setText(climatisation.type);
        binding.editTextNumberPuissance.setText(String.valueOf(climatisation.puissance));
        binding.editTextNumberQuantite.setText(String.valueOf(climatisation.quantite));
        binding.autoCompleteMarque.setText(climatisation.marque);
        binding.editTextModele.setText(climatisation.modele);
        binding.autoCompleteRegulations.setText(climatisation.regulation);
        binding.checkBoxAVerifierClimatisation.setChecked(climatisation.aVerifier);
        binding.editTextMultilineNoteClimatisation.setText(climatisation.note);
        viewZoneSelector.setSelectedZones(climatisation.zones);


        for (String uri : climatisation.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }
    private void prefillClimatisationName() {
        int index = 1;
        String element  = "Climatisation ";
        Releve releve =releveViewModel.getReleve().getValue();
        while(releve.climatisations.containsKey(element + index)){
            index++;
        }
        binding.editTextNomClimatisation.setText(element + index);

    }

    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }

}