package com.example.super_cep.view.fragments.Enveloppe;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.databinding.FragmentEnveloppeBinding;
import com.example.super_cep.model.Releve.Enveloppe.Eclairage;
import com.example.super_cep.model.Releve.Enveloppe.Menuiserie;
import com.example.super_cep.model.Releve.Enveloppe.Mur;
import com.example.super_cep.model.Releve.Enveloppe.Sol;
import com.example.super_cep.model.Releve.Enveloppe.Toiture;
import com.example.super_cep.model.Releve.Zone;
import com.example.super_cep.model.Releve.ZoneElement;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.view.AideFragment;

import org.apache.harmony.luni.util.NotImplementedException;

public class Enveloppe extends Fragment implements ZoneUiHandler, AideFragment {

    public Zone[] zones;

    private LiveData<Releve> releve;

    public FragmentEnveloppeBinding binding;
    private ReleveViewModel releveViewModel;

    private ZonesAdaptater zonesAdaptater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();
        binding = FragmentEnveloppeBinding.inflate(inflater, container, false);
        setupFab();
        setupRecyclerView(releve.getValue().zones.values().toArray(new Zone[0]));
        releve.observe(getViewLifecycleOwner(), releve -> {
            zones = releve.zones.values().toArray(new Zone[0]);
            updateRecyclerView(zones);
        });
        return binding.getRoot();
    }

    private void setupRecyclerView(Zone[] zones){
        RecyclerView recyclerView = binding.RecyclerViewZones;
        zonesAdaptater = new ZonesAdaptater(zones, this);
        binding.searchViewZonesElements.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                zonesAdaptater.getFilter().filter(newText);
                return true;
            }
        });
        recyclerView.setAdapter(zonesAdaptater);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void updateRecyclerView(Zone[] zones) {
        zonesAdaptater.updateZone(zones);
    }

    private void setupFab() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpZone.show(getContext(), new PopUpZoneHandler() {
                    @Override
                    public void nouvelleZone(String nomZone) {
                        Enveloppe.this.nouvelleZone(nomZone);
                    }

                    @Override
                    public void editZone(String oldNomZone, String newNomZone) {
                        throw new UnsupportedOperationException();
                    }
                }, releveViewModel);
            }
        });
    }


    @Override
    public void voirZoneElement(Zone zone, ZoneElement zoneElement) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        if(zoneElement instanceof Mur){
            EnveloppeDirections.ActionNavEnveloppesToFragmentMur action = EnveloppeDirections.actionNavEnveloppesToFragmentMur(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Toiture){
            EnveloppeDirections.ActionNavEnveloppesToFragmentToitureOuFauxPlafond action = EnveloppeDirections.actionNavEnveloppesToFragmentToitureOuFauxPlafond(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Menuiserie){
            EnveloppeDirections.ActionNavEnveloppesToFragmentMenuiserie action = EnveloppeDirections.actionNavEnveloppesToFragmentMenuiserie(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Sol){
            EnveloppeDirections.ActionNavEnveloppesToFragmentSol action = EnveloppeDirections.actionNavEnveloppesToFragmentSol(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else if(zoneElement instanceof Eclairage){
            EnveloppeDirections.ActionNavEnveloppesToFragmentEclairage action = EnveloppeDirections.actionNavEnveloppesToFragmentEclairage(zone.nom, zoneElement.nom, null);
            navController.navigate(action);
        }else {
            throw new IllegalArgumentException("ZoneElement non reconnu");
        }
    }

    @Override
    public void deleteZone(Zone zone) {
        releveViewModel.deleteZone(zone.nom);
    }

    @Override
    public void moveZoneElement(String nomZoneElement, String nomPreviousZone, String nomNewZone) {
            //ask if the user want a copy or a move
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Déplacer ou copier ?");
            builder.setMessage("Voulez vous déplacer ou copier l'élément ?");
            builder.setPositiveButton("Déplacer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {

                    releveViewModel.moveZoneElement(nomZoneElement, nomPreviousZone, nomNewZone);
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Copier", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    releveViewModel.copieZoneElement(nomZoneElement,nomPreviousZone, nomNewZone);
                }
            });
            builder.show();

    }

    @Override
    public void editNomZone(Zone zone) {
        PopUpZone.show(getContext(), new PopUpZoneHandler() {
            @Override
            public void nouvelleZone(String nomZone) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void editZone(String oldNomZone, String newNomZone) {
                try {
                    releveViewModel.editZone(oldNomZone, newNomZone);
                }catch (Exception e){
                    Log.e("Enveloppe", "editNomZone: ", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, releveViewModel, zone.nom);
    }


    @Override
    public void nouvelleElementZone(Zone zone) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        EnveloppeDirections.ActionNavEnveloppesToNavAjoutElementZone action =
                EnveloppeDirections.actionNavEnveloppesToNavAjoutElementZone(zone.nom);
        navController.navigate(action);
    }



    public void nouvelleZone(String toString) {
        try {
            releveViewModel.addZone(new Zone(toString));
        }catch (IllegalArgumentException e){
            Toast.makeText(getContext(), "une zone avec le même nom existe déjà", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void aide(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aide");

        //1. Ajout d'une nouvelle zone :
        //Pour ajouter une nouvelle zone, appuyez sur le bouton flottant (fab) situé en bas à droite de l'écran. Une fois le bouton pressé, une fenêtre contextuelle s'ouvrira pour vous demander le nom de la nouvelle zone.
        //
        //2. Modification du nom d'une zone :
        //Pour modifier le nom d'une zone, appuyez simplement sur la zone concernée. Une fenêtre contextuelle s'ouvrira pour vous permettre de modifier le nom.
        //
        //3. Suppression d'une zone :
        //Pour supprimer une zone, appuyez sur le bouton de suppression situé à côté de chaque zone. Un message de confirmation apparaîtra pour vous demander de confirmer la suppression de la zone. Veuillez noter que tous les éléments associés à la zone seront également supprimés.
        //
        //4. Ajout d'un nouvel élément dans une zone :
        //Chaque zone peut contenir plusieurs éléments. Pour ajouter un nouvel élément à une zone, appuyez sur le bouton d'ajout (+) situé à côté de chaque zone. Une nouvelle fenêtre s'ouvrira pour vous permettre de saisir les informations de l'élément.
        //
        //5. Déplacement des éléments d'une zone à une autre :
        //Pour déplacer un élément d'une zone à une autre, effectuez un appui long sur l'élément que vous souhaitez déplacer, puis faites-le glisser jusqu'à la zone de destination. Une boîte de dialogue vous demandera si vous souhaitez déplacer ou copier l'élément.
        //
        //6. Filtrage des éléments d'une zone :
        //Vous pouvez rechercher des éléments spécifiques dans une zone en utilisant la barre de recherche située en haut de l'écran. Commencez simplement à taper le nom de l'élément et la liste sera filtrée en conséquence.
        //
        //Nous espérons que ce guide vous sera utile. Si vous avez d'autres questions, n'hésitez pas à nous contacter.
        builder.setMessage("1. Ajout d'une nouvelle zone :\n" +
                "Pour ajouter une nouvelle zone, appuyez sur le bouton flottant (fab) situé en bas à droite de l'écran. Une fois le bouton pressé, une fenêtre contextuelle s'ouvrira pour vous demander le nom de la nouvelle zone.\n" +
                "\n" +
                "2. Modification du nom d'une zone :\n" +
                "Pour modifier le nom d'une zone, appuyez simplement sur la zone concernée. Une fenêtre contextuelle s'ouvrira pour vous permettre de modifier le nom.\n" +
                "\n" +
                "3. Suppression d'une zone :\n" +
                "Pour supprimer une zone, appuyez sur le bouton de suppression situé à côté de chaque zone. Un message de confirmation apparaîtra pour vous demander de confirmer la suppression de la zone. Veuillez noter que tous les éléments associés à la zone seront également supprimés.\n" +
                "\n" +
                "4. Ajout d'un nouvel élément dans une zone :\n" +
                "Chaque zone peut contenir plusieurs éléments. Pour ajouter un nouvel élément à une zone, appuyez sur le bouton d'ajout (+) situé à côté de chaque zone. Une nouvelle fenêtre s'ouvrira pour vous permettre de saisir les informations de l'élément.\n" +
                "\n" +
                "5. Déplacement des éléments d'une zone à une autre :\n" +
                "Pour déplacer un élément d'une zone à une autre, effectuez un appui long sur l'élément que vous souhaitez déplacer, puis faites-le glisser jusqu'à la zone de destination. Une boîte de dialogue vous demandera si vous souhaitez déplacer ou copier l'élément.\n" +
                "\n" +
                "6. Filtrage des éléments d'une zone :\n" +
                "Vous pouvez rechercher des éléments spécifiques dans une zone en utilisant la barre de recherche située en haut de l'écran. Commencez simplement à taper le nom de l'élément et la liste sera filtrée en conséquence.\n" +
                "\n" +
                "Nous espérons que ce guide vous sera utile. Si vous avez d'autres questions, n'hésitez pas à nous contacter.");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

}