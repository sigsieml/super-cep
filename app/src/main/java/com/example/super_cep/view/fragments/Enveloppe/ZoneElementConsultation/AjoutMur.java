package com.example.super_cep.view.fragments.Enveloppe.ZoneElementConsultation;

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
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentAjoutMurBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.view.ReleveViewModel;
import com.example.super_cep.view.SpinnerDataViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AjoutMur extends Fragment {

    private enum Mode{
        Ajout,
        Edition,
        Consultation
    }


    private static final String NOM_ZONE = "param1";

    private static final String NOM_ELEMENT = "param2";

    private static final String NOM_ANCIENNE_ZONE = "param3";
    private String nomZone;
    private String nomElement;

    private String nomAncienneZone;

    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public AjoutMur() {
        // Required empty public constructor
    }

    public static AjoutMur newInstance(String nomZone) {
        return newInstance(nomZone, null, null);
    }

    public static AjoutMur newInstance(String nomZone, String nomElement){
        return newInstance(nomZone, null, nomElement);
    }

    public static AjoutMur newInstance(String nouvelleZone,String ancienneZone, String nomElement) {
        AjoutMur fragment = new AjoutMur();
        Bundle args = new Bundle();
        args.putString(NOM_ZONE, nouvelleZone);
        args.putString(NOM_ELEMENT, nomElement);
        args.putString(NOM_ANCIENNE_ZONE, ancienneZone);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(NOM_ZONE);
        if(getArguments().getString(NOM_ANCIENNE_ZONE) != null){
            mode = Mode.Edition;
            nomElement = getArguments().getString(NOM_ELEMENT);
            nomAncienneZone = getArguments().getString(NOM_ANCIENNE_ZONE);
        }else if(getArguments().getString(NOM_ELEMENT) != null){
            mode = Mode.Consultation;
            nomElement = getArguments().getString(NOM_ELEMENT);
        }

    }

    private FragmentAjoutMurBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<String> launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    List<Uri> uriImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjoutMurBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        photoManager = new PhotoManager(getContext());
        setupPhotoLaunchers();
        updateSpinner();
        setupButtons();

        if(mode == Mode.Ajout || mode == Mode.Edition){
            addFooterAjout();
        }



        try {
            if(mode == Mode.Consultation){
                Mur mur =(Mur) releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement);
                setMondeConsultation(mur);
                addDataToView(mur);
            }
            if(mode == Mode.Edition){
                Mur mur =(Mur) releveViewModel.getReleve().getValue().getZone(nomAncienneZone).getZoneElement(nomElement);
                addDataToView(mur);
            }
        }catch (Exception e){
            Log.e("AjoutMur", "onCreateView: ", e);
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
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
                addMurToReleve();
            }
        });

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(Mur mur) {
        binding.textViewTitleMur.setText(mur.getNom());
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
                editMur();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeZoneElement(nomZone, nomElement);
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
    }

    private void addDataToView(Mur mur) {
        binding.editTextNomMur.setText(mur.getNom());
        binding.editTextNumberEpaisseurIsolant.setText(String.valueOf(mur.epaisseurIsolant));
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeMur, mur.typeMur);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeDeMiseEnOeuvre, mur.typeMiseEnOeuvre);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeIsolant, mur.typeIsolant);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerNiveauIsolation, mur.niveauIsolation);
        binding.checkBoxAVerifierMur.setChecked(mur.aVerifier);
        binding.editTextMultilineNoteMur.setText(mur.note);

        for (Uri uri : mur.uriImages) {
            addPhotoToView(uri);
        }
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
                                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), binding.buttonAjouterImage, "AjoutMur");
                                launcherGetPhoto.launch("image/*");
                                break;
                            case 1:
                                // Lancer l'activité de capture d'image
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                launcherCapturePhoto.launch(intent);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void updateSpinner() {
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeMur, "typeMur");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeDeMiseEnOeuvre, "typeDeMiseEnOeuvre");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeIsolant, "typeIsolant");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerNiveauIsolation, "niveauIsolation");
    }

    private void setupPhotoLaunchers() {
        launcherGetPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Uri selectedPhotoUri = result;
                Log.i("AjoutMur", "URI de la photo sélectionnée : " + selectedPhotoUri);
                addPhotoToView(selectedPhotoUri);
            }
        });

        launcherCapturePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Log.i("AjoutMur", "Bitmap de la photo prise : " + photoBitmap);
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                addPhotoToView(photo);
            }
        });
    }

    private void addPhotoToView(Uri selectedPhotoUri) {
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
                //get
                File file = new File(selectedPhotoUri.getPath());
                Uri fileUri = FileProvider.getUriForFile(getContext(), "com.example.super_cep.fileprovider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "image/*");
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
    }

    private void addMurToReleve() {
        try {
            releveViewModel.getReleve().getValue().getZone(nomZone).addZoneElement(getMurFromViews());
            releveViewModel.forceUpdateReleve();
            getParentFragmentManager().popBackStack();
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Snackbar.make(binding.getRoot(), "impossible d'ajouter le mur " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void editMur(){
        try {
            releveViewModel.editZoneElement(nomElement, nomZone, getMurFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Mur getMurFromViews() {
        Mur mur = new Mur(
                binding.editTextNomMur.getText().toString(),
                binding.spinnerTypeMur.getSelectedItem().toString(),
                binding.spinnerTypeDeMiseEnOeuvre.getSelectedItem().toString(),
                binding.spinnerTypeIsolant.getSelectedItem().toString(),
                binding.spinnerNiveauIsolation.getSelectedItem().toString(),
                Float.parseFloat(binding.editTextNumberEpaisseurIsolant.getText().toString()),
                binding.checkBoxAVerifierMur.isChecked(),
                binding.editTextMultilineNoteMur.getText().toString(),
                uriImages
        );
        return mur;
    }



}