package fr.sieml.super_cep.view.fragments.Remarques;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.controller.ConfigDataViewModel;
import fr.sieml.super_cep.databinding.FragmentRemarquesBinding;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.model.Releve.Remarque;
import fr.sieml.super_cep.view.AideFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FragmentRemarques extends Fragment implements AideFragment {


    private FragmentRemarquesBinding binding;
    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;
    Releve releve;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRemarquesBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        setupRecyclerView(releveViewModel.getReleve().getValue().remarques.values().toArray(new Remarque[0]));
        setupFabs();
        return binding.getRoot();
    }
    private void setupRecyclerView(Remarque[] remarques) {
        // order the remarques in the alphabetical order
        List<Remarque> orderedRemarque =Arrays.asList(remarques);
        Collections.sort(orderedRemarque);
        RemarqueAdapter remarqueAdapter = new RemarqueAdapter(orderedRemarque.toArray(new Remarque[0]), new RemarqueViewHolderListener() {
            @Override
            public void onClick(Remarque remarque) {
                // Alert Dialog to delete the remarque
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer la remarque");
                builder.setMessage("Voulez-vous vraiment supprimer cette remarque ?");
                builder.setPositiveButton("Oui", (dialog, which) -> {
                    releveViewModel.removeRemarque(remarque);

                });
                builder.setNegativeButton("Non", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }

            @Override
            public void onRemarqueEdited(String oldName, Remarque remarque) {
                releveViewModel.editRemarque(oldName,remarque);
            }


        });
        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            binding.recyclerViewRemarque.post(new Runnable() {
                @Override
                public void run() {
                    List<Remarque> orderedRemarque = new ArrayList<>(releve.remarques.values());
                    Collections.sort(orderedRemarque);
                    remarqueAdapter.update(orderedRemarque.toArray(new Remarque[0]));
                }
            });
        });

        binding.recyclerViewRemarque.setAdapter(remarqueAdapter);
        binding.recyclerViewRemarque.setLayoutManager(new LinearLayoutManager(getContext()));


    }


    private void setupFabs() {
        binding.floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getCurrentFocus() != null)
                    getActivity().getCurrentFocus().clearFocus();
                PopupNouvelleRemarque.create(getContext(),releveViewModel, configDataViewModel, new PopupNouvelleRemarqueListener() {
                    @Override
                    public void onValidate(Remarque remarque) {
                        try {
                            releveViewModel.addRemarque(remarque);
                        }catch (Exception e) {
                            Log.e("FragmentRemarques", "onValidate: ", e);
                            Toast.makeText(getContext(), "Erreur lors de l'ajout de la remarque : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void aide() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aide");
        builder.setMessage(
                "Bienvenue dans l'interface de gestion des remarques ! Les remarques sont utilisées sur chaque slide de votre présentation PowerPoint. Le nom de la remarque correspond au nom de la slide, ce qui vous permet d'ajouter facilement des informations pertinentes à chaque partie de votre présentation.\n\n" +
                        "Dans cette interface, vous pouvez gérer toutes vos remarques. Voici quelques astuces pour vous aider à vous y retrouver :\n\n" +
                        "- Affichage des remarques : Vos remarques sont affichées dans une liste, triées par ordre alphabétique pour faciliter leur localisation.\n\n" +
                        "- Ajout d'une remarque : Pour ajouter une nouvelle remarque, il suffit de cliquer sur le bouton flottant en bas à droite. Un formulaire apparaîtra, vous permettant d'entrer les informations nécessaires. Après avoir cliqué sur 'Valider', la nouvelle remarque sera ajoutée à la liste.\n\n" +
                        "- Édition d'une remarque : Si vous souhaitez modifier une remarque existante, il suffit de cliquer sur la remarque. Vous pouvez alors apporter les modifications nécessaires et valider pour mettre à jour la remarque dans la liste.\n\n" +
                        "- Suppression d'une remarque : Pour supprimer une remarque, faites un clic long sur celle-ci. Une fenêtre de dialogue apparaîtra, vous demandant de confirmer la suppression. En cliquant sur 'Oui', la remarque sera supprimée de la liste.\n\n" +
                        "Bonne utilisation !");
        builder.setPositiveButton("Merci, j'ai compris !", null);
        builder.show();
    }
}