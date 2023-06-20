package com.example.super_cep.view.fragments.Chauffages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentChauffageAjoutBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Chauffage;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.view.Mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentChauffageAjout extends Fragment {


    private static final String ARG_NOM_CHAUFFAGE = "param2";

    private String nomChauffage;


    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public FragmentChauffageAjout() {
        // Required empty public constructor
    }

    public static FragmentChauffageAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentChauffageAjout newInstance(String nomChauffage) {
        FragmentChauffageAjout fragment = new FragmentChauffageAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_CHAUFFAGE, nomChauffage);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_CHAUFFAGE) != null){
            mode = Mode.Edition;
            nomChauffage = getArguments().getString(ARG_NOM_CHAUFFAGE);
        }

    }

    private FragmentChauffageAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<Intent> launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    List<Uri> uriImages = new ArrayList<>();

    List<String> typeChauffageProducteur = new ArrayList<>();
    List<String> typeChauffageEmetteur = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChauffageAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        photoManager = new PhotoManager(getContext());
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        Map<String, List<String>> spinnerLiveData = spinnerDataViewModel.getSpinnerData().getValue();
        if(!spinnerLiveData.containsKey("typeChauffageEmetteur") || !spinnerLiveData.containsKey("typeChauffageProducteur")){
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return binding.getRoot();
        }
        typeChauffageEmetteur = spinnerLiveData.get("typeChauffageEmetteur");
        typeChauffageProducteur = spinnerLiveData.get("typeChauffageProducteur");
        setupPhotoLaunchers();
        updateSpinner();
        addZonesToZonesList();
        setupButtons();

        if(mode == Mode.Ajout){
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                Chauffage chauffage = releveViewModel.getReleve().getValue().chauffages.get(nomChauffage);
                setModeEdition(chauffage);
                addDataToView(chauffage);
            }
        }catch (Exception e){
            Log.e("Ajout chauffage", "onCreateView: ", e);
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
                getParentFragmentManager().popBackStack();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChauffageToReleve();
            }
        });

        binding.linearLayoutAjoutChauffage.addView(viewFooter.getRoot());
    }

    private void setModeEdition(Chauffage chauffage) {
        binding.textViewTitleChauffage.setText(chauffage.nom);
        ViewFooterZoneElementConsultationBinding viewFooter = ViewFooterZoneElementConsultationBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editChauffage();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeChauffage(nomChauffage);
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutChauffage.addView(viewFooter.getRoot());
    }



    private void setupButtons() {
        binding.buttonAjouterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une boîte de dialogue pour choisir entre sélectionner une photo ou prendre une photo
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choisir une option");
                builder.setItems(new CharSequence[]{"Sélectionner une photo", "Prendre une photo"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:// Lancer l'activité de sélection de photo
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                launcherGetPhoto.launch(intent);
                                break;
                            case 1:
                                // Lancer l'activité de capture d'image
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                launcherCapturePhoto.launch(intent2);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }



    private void setupPhotoLaunchers() {

        launcherGetPhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        // Faites ce que vous devez faire avec l'URI ici
                        addPhotoToView(uri);

                    }
                });


        launcherCapturePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                addPhotoToView(photo);
            }
        });
    }

    private void addPhotoToView(Uri selectedPhotoUri) {
        try {
            uriImages.add(selectedPhotoUri);

            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_image_zone_element, null);
            int widthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthInDp, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            binding.linearLayoutImageViewsZoneElement.addView(view, 0);
            ViewImageZoneElementBinding viewImageZoneElementBinding = ViewImageZoneElementBinding.bind(view);
            viewImageZoneElementBinding.imageViewZoneElement.setImageURI(selectedPhotoUri);
            viewImageZoneElementBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtenez l'URI de FileProvider
                    Uri contentUri = null;
                    if (selectedPhotoUri.getScheme().equals("content")) {
                        contentUri = selectedPhotoUri;
                    } else if (selectedPhotoUri.getScheme().equals("file")) {
                        contentUri = photoManager.getFileProviderUri(selectedPhotoUri);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(contentUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent);
                }
            });

            viewImageZoneElementBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // show a dialog to confirm the deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Supprimer l'image ?");
                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            binding.linearLayoutImageViewsZoneElement.removeView(view);
                            uriImages.remove(selectedPhotoUri);
                        }
                    });
                    builder.setNegativeButton("Non", null);
                    builder.show();
                    return true;
                }
            });

        }catch (Exception e){
            Log.e("Ajout image", "addPhotoToView: ", e);
            Toast.makeText(getContext(), "Erreur avec l'image elle est donc supprimer", Toast.LENGTH_SHORT).show();
            uriImages.remove(selectedPhotoUri);
            releveViewModel.getReleve().getValue().chauffages.get(nomChauffage).uriImages = uriImages;
            releveViewModel.forceUpdateReleve();
        }

    }

    private void addChauffageToReleve() {
        try {
            releveViewModel.addChauffage(getChauffageFromViews());
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Log.e("Chauffage", "addChauffageToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void editChauffage(){
        try {
            releveViewModel.editChauffage(nomChauffage, getChauffageFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e("Chauffage", "addChauffageToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getSelectedZones(){
        List<String> selectedZones = new ArrayList<>();
        for (int i = 0; i < binding.linearLayoutZones.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) binding.linearLayoutZones.getChildAt(i);
            if (checkBox.isChecked()){
                selectedZones.add(checkBox.getText().toString());
            }
        }
        return selectedZones;
    }

    private void addZonesToZonesList(){
        Zone[] zones = releveViewModel.getReleve().getValue().getZonesValues();
        for (int i = 0; i < zones.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(zones[i].nom);
            binding.linearLayoutZones.addView(checkBox);
        }
    }

    private void setSelectedZones(List<String> selectedZones){
        for (int i = 0; i < binding.linearLayoutZones.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) binding.linearLayoutZones.getChildAt(i);
            if (selectedZones.contains(checkBox.getText())){
                checkBox.setChecked(true);
            }
        }
    }

    private void updateSpinner() {
        List<String> customList = new ArrayList<>();
        customList.addAll(typeChauffageProducteur);
        customList.addAll(typeChauffageEmetteur);

        SpinnerDataViewModel.updateSpinnerData(binding.spinnerTypeChauffage, customList);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerRegulations, "regulationChauffage");

        binding.spinnerTypeChauffage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(estProducteur()){
                    spinnerDataViewModel.updateSpinnerData(binding.spinnerMarque, "marqueProducteur");
                }else{
                    spinnerDataViewModel.updateSpinnerData(binding.spinnerMarque, "marqueEmetteur");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private boolean estProducteur(){
        return typeChauffageProducteur.contains(binding.spinnerTypeChauffage.getSelectedItem().toString());
    }

    private Chauffage getChauffageFromViews(){
            Chauffage chauffage = new Chauffage(
                    binding.editTextNomChauffage.getText().toString(),
                    binding.spinnerTypeChauffage.getSelectedItem().toString(),
                    binding.editTextNumberPuissance.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextNumberPuissance.getText().toString()),
                    binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                    binding.spinnerMarque.getSelectedItem().toString(),
                    binding.editTextModele.getText().toString(),
                    getSelectedZones(),
                    estProducteur(),
                    binding.spinnerRegulations.getSelectedItem().toString(),
                    uriImages,
                    binding.checkBoxAVerifierChauffage.isChecked(),
                    binding.editTextMultilineNoteChauffage.getText().toString()
            );

        return chauffage;
    }

    private void addDataToView(Chauffage chauffage){
        binding.editTextNomChauffage.setText(chauffage.nom);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeChauffage, chauffage.type);
        binding.editTextNumberPuissance.setText(String.valueOf(chauffage.puissance));
        binding.editTextNumberQuantite.setText(String.valueOf(chauffage.quantite));
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerMarque, chauffage.marque);
        binding.editTextModele.setText(chauffage.modele);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerRegulations, chauffage.regulation);
        binding.checkBoxAVerifierChauffage.setChecked(chauffage.aVerifier);
        binding.editTextMultilineNoteChauffage.setText(chauffage.note);

        setSelectedZones(chauffage.zones);
        for (Uri uri : chauffage.uriImages) {
            addPhotoToView(uri);
        }
    }

}