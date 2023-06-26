package com.example.super_cep.view.fragments.Enveloppe.ZoneElements;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentEclairageBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.ZoneElement;
import com.example.super_cep.view.Mode;

import java.util.ArrayList;
import java.util.List;

public class FragmentEclairage extends Fragment {

    private static final String NOM_ZONE = "param1";

    private static final String NOM_ELEMENT = "param2";

    private static final String NOM_ANCIENNE_ZONE = "param3";
    private String nomZone;
    private String nomElement;

    private String nomAncienneZone;

    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public FragmentEclairage() {
        // Required empty public constructor
    }

    public static FragmentEclairage newInstance(String nomZone) {
        return newInstance(nomZone, null, null);
    }

    public static FragmentEclairage newInstance(String nomZone, String nomElement){
        return newInstance(nomZone, null, nomElement);
    }

    public static FragmentEclairage newInstance(String nouvelleZone,String ancienneZone, String nomElement) {
        FragmentEclairage fragment = new FragmentEclairage();
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

    private FragmentEclairageBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<Intent> launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    List<Uri> uriImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEclairageBinding.inflate(inflater, container, false);
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
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement);
                setMondeConsultation(zoneElement);
                addDataToView(zoneElement);
            }
            if(mode == Mode.Edition){
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomAncienneZone).getZoneElement(nomElement);
                addDataToView(zoneElement);
            }
        }catch (Exception e){
            Log.e("AjoutZoneElement", "onCreateView: ", e);
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
                addZoneElementToReleve();
            }
        });

        binding.linearLayoutAjoutEclairage.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleEclairage.setText(zoneElement.getNom());
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
                editZoneElement();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeZoneElement(nomZone, nomElement);
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutEclairage.addView(viewFooter.getRoot());
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
            Log.e("AjoutZoneElement", "addPhotoToView: ", e);
            Toast.makeText(getContext(), "Erreur avec les images elles sont donc supprimer", Toast.LENGTH_SHORT).show();
            uriImages.remove(selectedPhotoUri);
            List<String> images = new ArrayList<>();
            for (Uri uri : uriImages) {
                images.add(uri.toString());
            }
            releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement).images = images;
            releveViewModel.forceUpdateReleve();
        }

    }

    private void addZoneElementToReleve() {
        try {
            releveViewModel.getReleve().getValue().getZone(nomZone).addZoneElement(getZoneElementFromViews());
            releveViewModel.forceUpdateReleve();
            getParentFragmentManager().popBackStack();
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Toast.makeText(getContext(), "Impossible d'ajouter l'élément", Toast.LENGTH_SHORT).show();
        }
    }

    private void editZoneElement(){
        try {
            releveViewModel.editZoneElement(nomElement, nomZone, getZoneElementFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSpinner() {
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeEclairage, "typeEclairage");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeRegulation, "typeRegulation");
    }


    private ZoneElement getZoneElementFromViews() {
        List<String> images = new ArrayList<>();
        for (Uri uri : uriImages) {
            images.add(uri.toString());
        }
        Eclairage eclairage = new Eclairage(
                binding.editTextNomEclairage.getText().toString(),
                binding.spinnerTypeEclairage.getSelectedItem().toString(),
                binding.spinnerTypeRegulation.getSelectedItem().toString(),
                binding.checkBoxAVerifierEclairage.isChecked(),
                binding.editTextMultilineNoteEclairage.getText().toString(),
                images
        );
        return eclairage;
    }

    private void addDataToView(ZoneElement zoneElement){
        Eclairage eclairage = (Eclairage) zoneElement;
        binding.editTextNomEclairage.setText(eclairage.getNom());
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeEclairage, eclairage.typeEclairage);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeRegulation, eclairage.typeDeRegulation);
        binding.checkBoxAVerifierEclairage.setChecked(eclairage.aVerifier);
        binding.editTextMultilineNoteEclairage.setText(eclairage.note);

        for (String uri : eclairage.images) {
            addPhotoToView(Uri.parse(uri));
        }
    }

}