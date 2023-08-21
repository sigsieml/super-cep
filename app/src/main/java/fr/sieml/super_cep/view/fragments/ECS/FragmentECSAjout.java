package fr.sieml.super_cep.view.fragments.ECS;

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
import fr.sieml.super_cep.databinding.FragmentECSAjoutBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementBinding;
import fr.sieml.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import fr.sieml.super_cep.model.Releve.ECS;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.view.Mode;
import fr.sieml.super_cep.view.includeView.ViewPhoto;
import fr.sieml.super_cep.view.includeView.ViewZoneSelector;

import java.util.ArrayList;
import java.util.List;

public class FragmentECSAjout extends Fragment {

    private static final String ARG_NOM_ECS = "NOM_ECS";

    private String nomECS;


    private Mode mode = Mode.Ajout;
    public FragmentECSAjout() {
        // Required empty public constructor
    }

    public static FragmentECSAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentECSAjout newInstance(String nomECS) {
        FragmentECSAjout fragment = new FragmentECSAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_ECS, nomECS);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_ECS) != null){
            mode = Mode.Edition;
            nomECS = getArguments().getString(ARG_NOM_ECS);
        }

    }

    private FragmentECSAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;


    private ViewZoneSelector viewZoneSelector;
    private ViewPhoto viewPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentECSAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        viewPhoto = new ViewPhoto(binding.includeViewPhoto, this);
        viewPhoto.setupPhotoLaunchers();
        updateSpinner();
        viewZoneSelector = new ViewZoneSelector(binding.includeZoneSelection, releveViewModel);

        if(mode == Mode.Ajout){
            prefillECSName();
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                ECS ecs = releveViewModel.getReleve().getValue().ecs.get(nomECS);
                setModeEdition(ecs);
                addDataToView(ecs);
            }
        }catch (Exception e){
            Log.e("Ajout ecs", "onCreateView: ", e);
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
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
                addECSToReleve();
            }
        });

        binding.linearLayoutAjoutECS.addView(viewFooter.getRoot());
    }

    private void setModeEdition(ECS ecs) {
        binding.textViewTitleECS.setText(ecs.nom);
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
                editECS();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeECS(nomECS);
                back();
            }
        });

        binding.linearLayoutAjoutECS.addView(viewFooter.getRoot());
    }


    private void addECSToReleve() {
        try {
            releveViewModel.addECS(getECSFromViews());
            back();

        }catch (Exception e){
            Log.e("ECS", "addECSToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editECS(){
        try {
            releveViewModel.editECS(nomECS, getECSFromViews());
            back();
        } catch (Exception e) {
            Log.e("ECS", "addECSToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSpinner() {
        configDataViewModel.setAutoComplete(binding.autoCompleteTypeECS, "typeECS");
        configDataViewModel.setAutoComplete(binding.autoCompleteMarque, "marqueECS");
    }

    private ECS getECSFromViews(){
        List<String> images = new ArrayList<>();
        for(Uri uri : viewPhoto.getUriImages()){
            images.add(uri.toString());
        }
        ECS ecs = new ECS(
                binding.editTextNomECS.getText().toString(),
                binding.autoCompleteTypeECS.getText().toString(),
                binding.editTextModele.getText().toString(),
                binding.autoCompleteMarque.getText().toString(),
                binding.editTextVolume.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextVolume.getText().toString()),
                binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                viewZoneSelector.getSelectedZones(),
                images,
                binding.checkBoxAVerifierECS.isChecked(),
                binding.editTextMultilineNoteECS.getText().toString()
        );

        return ecs;
    }

    private void addDataToView(ECS ecs){
        binding.editTextNomECS.setText(ecs.nom);
        binding.autoCompleteTypeECS.setText(ecs.type);
        binding.editTextModele.setText(ecs.modele);
        binding.autoCompleteMarque.setText(ecs.marque);
        binding.editTextVolume.setText(String.valueOf(ecs.volume));
        binding.editTextNumberQuantite.setText(String.valueOf(ecs.quantite));

        viewZoneSelector.setSelectedZones(ecs.zones);
        binding.checkBoxAVerifierECS.setChecked(ecs.aVerifier);
        binding.editTextMultilineNoteECS.setText(ecs.note);

        for (String uri : ecs.images) {
            viewPhoto.addPhotoToView(Uri.parse(uri));
        }
    }

    private void prefillECSName() {
        int index = 1;
        String element  = "ECS ";
        Releve releve =releveViewModel.getReleve().getValue();
        while(releve.ecs.containsKey(element + index)){
            index++;
        }
        binding.editTextNomECS.setText(element + index);

    }

    private void back(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.popBackStack();
    }

}