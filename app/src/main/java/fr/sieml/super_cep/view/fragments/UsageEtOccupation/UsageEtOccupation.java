package fr.sieml.super_cep.view.fragments.UsageEtOccupation;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.databinding.FragmentUsageEtOccupationBinding;
import fr.sieml.super_cep.model.Releve.Calendrier.Calendrier;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.model.Releve.Zone;
import fr.sieml.super_cep.view.AideFragment;

public class UsageEtOccupation extends Fragment implements AideFragment {


    private ReleveViewModel releveViewModel;
    private FragmentUsageEtOccupationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsageEtOccupationBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();
        setupRecyclerView(releve.calendriers.values().toArray(new Calendrier[0]), releve.zones.values().toArray(new Zone[0]));

        binding.floatingActionButtonAjoutCalendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopUpAjoutCalendrier.create(getContext(), releveViewModel.getReleve().getValue().zones.values().toArray(new Zone[0]),
                        releveViewModel.getNextNameForCalendrier(), new PopUpAjoutCalendrierListener() {
                            @Override
                            public void onValider(Calendrier calendrier) {
                                try {
                                    releveViewModel.addCalendrier(calendrier);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return binding.getRoot();
    }

    private void setupRecyclerView(Calendrier[] calendriersValues, Zone[] zones) {
        CalendrierAdaptater calendrierAdaptater = new CalendrierAdaptater(calendriersValues, zones, new CalendrierViewHolderListener() {
            @Override
            public void onClick(Calendrier calendrier) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                fr.sieml.super_cep.view.fragments.UsageEtOccupation.UsageEtOccupationDirections.ActionNavUsageEtOccupationToFragmentUsageEtOccupationCalendrier action =
                        UsageEtOccupationDirections.actionNavUsageEtOccupationToFragmentUsageEtOccupationCalendrier(calendrier.nom);
                navController.navigate(action);
            }

            @Override
            public void onLongClick(Calendrier calendrier) {
                PopUpModificationCalendrier.create(getContext(), zones, calendrier, new PopUpModificationCalendrierListener() {
                    @Override
                    public void onValider(String oldName, Calendrier calendrier) {
                        try {
                            releveViewModel.updateCalendrier(oldName, calendrier);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSupprimer(String nomCalendrier) {
                        releveViewModel.supprimerCalendrier(nomCalendrier);
                    }
                });

            }
        });

        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            calendrierAdaptater.updateValues(releve.calendriers.values().toArray(new Calendrier[0]), releve.zones.values().toArray(new Zone[0]));
        });
        binding.recyclerViewCalendrier.setAdapter(calendrierAdaptater);
        binding.recyclerViewCalendrier.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
    }


    @Override
    public void aide() {
        AlertDialog.Builder constructeurAide = new AlertDialog.Builder(getContext());
        constructeurAide.setTitle("Aide");
        constructeurAide.setMessage(
                "Dans cette interface, vous pouvez facilement visualiser et gérer les différents calendriers de votre application.\n\n" +
                        "- Visualiser un calendrier : Un simple clic sur un calendrier vous permet de consulter ses détails. Un fragment apparaîtra, où vous pouvez indiquer pour chaque demi-heure l'état du chauffage et l'occupation du calendrier.\n\n" +
                        "- Modifier un calendrier : Si vous souhaitez apporter des modifications à un calendrier, maintenez le clic sur celui-ci. Cette action ouvre le menu de modification.\n\n" +
                        "- Les zones d'un calendrier : Pour chaque calendrier, vous trouverez la liste de ses zones associées. Elles sont affichées sous le nom du calendrier.\n\n"
        );
        constructeurAide.setPositiveButton("Compris, merci !", null);
        constructeurAide.show();

    }
}