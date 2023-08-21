package fr.sieml.super_cep.view.fragments.ExportData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.sieml.super_cep.controller.Conso.ConsoConfigViewModel;

import fr.sieml.super_cep.databinding.FragmentExportdataBinding;
import fr.sieml.super_cep.model.Export.ArchiveExporter;
import fr.sieml.super_cep.model.Export.PowerpointExporter;
import fr.sieml.super_cep.controller.ReleveViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Exportdata extends Fragment {

    FragmentExportdataBinding binding;
    ReleveViewModel releveViewModel;
    private ActivityResultLauncher<Intent> createFileActivityResultLauncher;
    private ActivityResultLauncher<Intent> createArchiveActivityResultLauncher;
    private ConsoConfigViewModel consoConfigViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExportdataBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        consoConfigViewModel = new ViewModelProvider(requireActivity()).get(ConsoConfigViewModel.class);
        createLauncherIntent();
        binding.buttonExporterLesDonne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPowerpointFile();
            }
        });
        binding.buttonCreerUneArchive.setOnClickListener((view) -> createArchiveFile());
        return binding.getRoot();
    }
    private void createLauncherIntent() {
        createFileActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Votre logique ici
                        Intent data = result.getData();
                        // Faire quelque chose avec les données d'intention
                        Uri uri = null;
                        if (data != null) {
                            uri = data.getData();
                            writeToFile(uri);
                        }
                    }
                }
        );

        createArchiveActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Votre logique ici
                        Intent data = result.getData();
                        // Faire quelque chose avec les données d'intention
                        Uri uri;
                        if (data != null) {
                            uri = data.getData();
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle("Création de l'archive");
                            builder.setMessage("Json ou CSV ?");
                            builder.setPositiveButton("CSV", (dialog, which) -> {
                                writeArchive(uri, false);
                            });
                            builder.setNegativeButton("JSON", (dialog, which) -> {
                                writeArchive(uri, true);
                            });
                            builder.show();
                        }
                    }
                });
    }
    private void createPowerpointFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        intent.putExtra(Intent.EXTRA_TITLE, releveViewModel.getReleve().getValue().nomBatiment + "releve.pptx");
        // Lancement de l'ActivityResultLauncher
        createFileActivityResultLauncher.launch(intent);
    }
    private void createArchiveFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_TITLE, releveViewModel.getReleve().getValue().nomBatiment + "releve.zip");
        // Lancement de l'ActivityResultLauncher
        createArchiveActivityResultLauncher.launch(intent);
    }
    private void writeToFile(Uri uri) {
        final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        final LoadingPopup loadingPopup = LoadingPopup.create(getContext());
        final Executor mainThreadExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ParcelFileDescriptor pfd = requireActivity().getContentResolver().
                            openFileDescriptor(uri, "w");
                    PowerpointExporter powerpointExporter = new PowerpointExporter( new AndroidProvider(getContext()),  consoConfigViewModel.getConsoParser().getValue(), binding.seekbarQualite.getProgress());
                    powerpointExporter.export(getContext().getAssets().open(PowerpointExporter.POWERPOINT_VIERGE_NAME),
                            pfd.getFileDescriptor(),
                            releveViewModel.getReleve().getValue(),
                            consoConfigViewModel.getNomBatimentConso(),
                            consoConfigViewModel.getAnneesConso(),
                            consoConfigViewModel.getMeilleurAnne(),
                            consoConfigViewModel.getPourcentageBatiment()
                    );

                    pfd.close();

                    mainThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadingPopup.terminer();
                        }
                    });
                    showFileToUser(uri,"application/vnd.openxmlformats-officedocument.presentationml.presentation" );

                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadingPopup.erreur(e.getMessage());
                        }
                    });
                }
            }
        });

    }

    private void showFileToUser(Uri uri, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }



    private void writeArchive(Uri uri, boolean jsonOrCsv) {
        final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        final LoadingPopup loadingPopup = LoadingPopup.create(getContext());
        final Executor mainThreadExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArchiveExporter.createArchive( getContext().getContentResolver().openOutputStream(uri),releveViewModel.getReleve().getValue(), new AndroidProvider(getContext()), binding.seekbarQualite.getProgress(), jsonOrCsv);

                    mainThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadingPopup.terminer();
                        }
                    });
                    showFileToUser(uri, "application/zip");

                } catch (Exception e) {
                    e.printStackTrace();
                    mainThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadingPopup.erreur(e.getMessage());
                        }
                    });
                }
            }
        });

    }

}