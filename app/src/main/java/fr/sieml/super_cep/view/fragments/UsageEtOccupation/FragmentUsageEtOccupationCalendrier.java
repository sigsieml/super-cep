package fr.sieml.super_cep.view.fragments.UsageEtOccupation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.controller.ReleveViewModel;
import fr.sieml.super_cep.databinding.FragmentUeoCalendrierBinding;
import fr.sieml.super_cep.databinding.ViewUeoCalendrierJourBinding;
import fr.sieml.super_cep.databinding.ViewUsageEtOccupationHeureBinding;
import fr.sieml.super_cep.model.Releve.Calendrier.Calendrier;
import fr.sieml.super_cep.model.Releve.Calendrier.CalendrierDate;
import fr.sieml.super_cep.model.Releve.Calendrier.ChaufferOccuper;
import fr.sieml.super_cep.model.Releve.Releve;
import fr.sieml.super_cep.view.AideFragment;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentUsageEtOccupationCalendrier extends Fragment implements AideFragment {

    private static final String ARG_CALENDRIER = "nomCalendrier";
    private String nomCalendrier;

    public FragmentUsageEtOccupationCalendrier() {
        // Required empty public constructor
    }

    public static FragmentUsageEtOccupationCalendrier create(String param1) {
        FragmentUsageEtOccupationCalendrier fragment = new FragmentUsageEtOccupationCalendrier();
        Bundle args = new Bundle();
        args.putString(ARG_CALENDRIER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomCalendrier = requireArguments().getString(ARG_CALENDRIER);
    }

    FragmentUeoCalendrierBinding binding;

    ReleveViewModel releveViewModel;

    private boolean modeChauffage = true;


    private Drawable chaufferOccuperDrawable;
    private Drawable chaufferDrawable;
    private Drawable occuperDrawable;
    private Drawable videDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUeoCalendrierBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();
        Calendrier calendrier = releve.calendriers.get(nomCalendrier);
        binding.textViewNomCalendrier.setText(calendrier.nom);
        binding.textViewZoneCalendrier.setText(zonesToString(calendrier.zones));

        chaufferOccuperDrawable = getResources().getDrawable(R.drawable.border_green);
        chaufferDrawable = getResources().getDrawable(R.drawable.shape_red_corners);
        occuperDrawable = getContext().getResources().getDrawable(R.drawable.border_purple700);
        videDrawable = getContext().getResources().getDrawable(R.drawable.border_black_thin);


        setupToggleButton();
        setupCalendrier(calendrier);


        return binding.getRoot();
    }


    private boolean modeAjout = false;
    private boolean scrollable = true;
    private Map<View, Integer> mapLastDoubleTap = new HashMap<>();
    private int timeStampDoubleTap = 0;

    @SuppressLint("ClickableViewAccessibility")
    private void setupCalendrier(Calendrier calendrier) {

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                Log.i("GESTURE", "onDoubleTap");
                modeAjout = true;
                scrollable = false;
                timeStampDoubleTap = (int) System.currentTimeMillis();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
                Log.i("GESTURE", "onSingleTap");
                timeStampDoubleTap = (int) System.currentTimeMillis();
                handleTouch(event.getRawX(), event.getRawY());
                return super.onSingleTapConfirmed(event);
            }

        });

        final View.OnTouchListener scroolViewOnTouchListener = (View v, MotionEvent event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                modeAjout = false;
                scrollable = true;
                gestureDetector.onTouchEvent(event);
                return true;
            }

            if (modeAjout == true && (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN )) {
                Log.i("GESTURE", "onTouch" + modeAjout);
                handleTouch(event.getRawX(), event.getRawY());
            }
            return gestureDetector.onTouchEvent(event) || !scrollable;
        };

        binding.scroolViewHorizontal.setOnTouchListener(scroolViewOnTouchListener);
        binding.scroolViewJours.setOnTouchListener(scroolViewOnTouchListener);


        childsHeurs = new View[7 * 48];
        childsJours = new View[7];

        Map<CalendrierDate, ChaufferOccuper> map = calendrier.calendrierDateChaufferOccuperMap;
        for (int i = 0; i < 7; i++) {
            ViewUeoCalendrierJourBinding bindingViewJour = ViewUeoCalendrierJourBinding.inflate(getLayoutInflater());
            final String[] weekDay = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
            bindingViewJour.textViewJour.setText(weekDay[i]);

            for (float j = 0; j < 24; j += 0.5) {

                ViewUsageEtOccupationHeureBinding bindingViewHeure = ViewUsageEtOccupationHeureBinding.inflate(getLayoutInflater());
                bindingViewHeure.textViewHeur.setText(String.valueOf((int)Math.floor(j)) + ":" + String.valueOf((int)((j % 1) * 60)));
                bindingViewJour.linearLayoutJour.addView(bindingViewHeure.getRoot());

                CalendrierDate calendrierDate = getCalendrierDateFromIndex((int) ((i * 48) + (j * 2)));
                if(map.containsKey(calendrierDate)){
                    ChaufferOccuper chaufferOccuper = map.get(calendrierDate);
                    if(chaufferOccuper == ChaufferOccuper.CHAUFFER_OCCUPER){
                        bindingViewHeure.getRoot().setBackground(occuperDrawable);
                        bindingViewHeure.imageViewFire.setVisibility(View.VISIBLE);
                    }else if(chaufferOccuper == ChaufferOccuper.CHAUFFER) {
                        bindingViewHeure.imageViewFire.setVisibility(View.VISIBLE);
                    }else if(chaufferOccuper == ChaufferOccuper.OCCUPER){
                        bindingViewHeure.imageViewFire.setVisibility(View.INVISIBLE);
                        bindingViewHeure.getRoot().setBackground(occuperDrawable);
                    }
                }
                childsHeurs[(int)(i * 48 + j * 2)] = bindingViewHeure.getRoot();
            }

            binding.linearLayoutJours.addView(bindingViewJour.getRoot());
            childsJours[i] = bindingViewJour.getRoot();
        }


    }



    private View[] childsHeurs;
    private View[] childsJours;

    private void handleTouch(float x, float y){
        for (int i = 0; i < childsJours.length; i++) {
            View childJour = childsJours[i];
            if(isPointInsideView(x,y, childJour)){
                for(int j = 0; j < 48; j++){
                    View childHeur = childsHeurs[i * 48 + j];
                    if (isPointInsideView(x, y, childHeur)) {
                        if(mapLastDoubleTap.containsKey(childHeur) && mapLastDoubleTap.get(childHeur) == timeStampDoubleTap){
                            break;
                        }
                        mapLastDoubleTap.put(childHeur, timeStampDoubleTap);
                        ViewUsageEtOccupationHeureBinding bindingViewHeure = ViewUsageEtOccupationHeureBinding.bind(childHeur);
                        if(modeChauffage){
                            if(bindingViewHeure.imageViewFire.getVisibility() == View.VISIBLE){
                                bindingViewHeure.imageViewFire.setVisibility(View.INVISIBLE);
                            }else {
                                bindingViewHeure.imageViewFire.setVisibility(View.VISIBLE);
                            }
                        }else{
                            if(bindingViewHeure.getRoot().getBackground() == occuperDrawable){
                                bindingViewHeure.getRoot().setBackground(videDrawable);
                            }else {
                                bindingViewHeure.getRoot().setBackground(occuperDrawable);
                            }
                        }
                    }
                }
                break;
            }
        }
    }


    private String zonesToString(List<String> zones){
        StringBuilder stringBuilder = new StringBuilder("zones : ");
        for (String zone : zones) {
            //check if zone is in zones
            stringBuilder.append(zone);
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    private void setupToggleButton() {
        binding.toggleButtonChauffage.setTextSize(20);
        binding.toggleButtonChauffage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonChauffage.setTextSize(20);
                binding.toggleButtonOccupation.setTextSize(14);

                modeChauffage = true;

            }
        });

        binding.toggleButtonOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonChauffage.setTextSize(14);
                binding.toggleButtonOccupation.setTextSize(20);
                modeChauffage = false;

            }
        });


    }

    private boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        return ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight())));
    }

    @Override
    public void onPause() {
        Toast.makeText(getContext(), "onPause", Toast.LENGTH_SHORT).show();
        saveData();
        super.onPause();
    }

    private CalendrierDate getCalendrierDateFromIndex(int index){
        final DayOfWeek[] dayOfWeek = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        int jour = index / 48;
        int heur = index % 48;
        CalendrierDate calendrierDate = new CalendrierDate(dayOfWeek[jour], heur / 2, heur % 2 == 0 ? 0 : 30);
        return calendrierDate;
    }

    private void saveData() {
        Releve releve = releveViewModel.getReleve().getValue();
        Calendrier calendrier = releve.calendriers.get(nomCalendrier);
        Map<CalendrierDate, ChaufferOccuper> map =  new HashMap<>();

        for (int i = 0; i < childsHeurs.length; i++) {
            View childHeur = childsHeurs[i];
            ViewUsageEtOccupationHeureBinding bindingViewHeure = ViewUsageEtOccupationHeureBinding.bind(childHeur);
            ChaufferOccuper chaufferOccuper;
            if(bindingViewHeure.getRoot().getBackground() == occuperDrawable && bindingViewHeure.imageViewFire.getVisibility() == View.VISIBLE){
                chaufferOccuper = ChaufferOccuper.CHAUFFER_OCCUPER;
            }else if(bindingViewHeure.imageViewFire.getVisibility() == View.VISIBLE) {
                chaufferOccuper = ChaufferOccuper.CHAUFFER;
            }else if(bindingViewHeure.getRoot().getBackground() == occuperDrawable) {
                chaufferOccuper = ChaufferOccuper.OCCUPER;
            }else{
                continue;
            }
            CalendrierDate calendrierDate = getCalendrierDateFromIndex(i);
            map.put(calendrierDate, chaufferOccuper);
        }
        calendrier.calendrierDateChaufferOccuperMap = map;
        releveViewModel.setReleve(releve);
        Toast.makeText(getContext(), "calendrier sauvegardé", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aide() {
        // Afficher la fenêtre d'aide
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aide");
        builder.setMessage(
                "Dans cet écran, vous pouvez visualiser et modifier le calendrier d'occupation et d'usage pour le calendrier choisi.\n\n" +
                        "Les différentes actions que vous pouvez effectuer sont les suivantes :\n" +
                        "- Vous pouvez basculer entre le mode de chauffage et le mode d'occupation en utilisant les boutons de basculement situés en haut de l'écran. Le mode actif est indiqué par le texte en gras.\n" +
                        "- Vous pouvez ajouter ou supprimer des périodes de chauffage ou d'occupation en tapotant une fois sur une heure spécifique dans le calendrier. En mode de chauffage, cela affiche ou masque une icône de feu. En mode d'occupation, cela change la couleur de fond de l'heure.\n" +
                        "- Vous pouvez ajouter ou supprimer plusieurs périodes de chauffage ou d'occupation en faisant un double tap sur une heure pour commencer et en glissant votre doigt vers les heures suivantes ou précédentes.\n\n" +
                        "Lorsque vous quittez l'écran, vos modifications sont automatiquement sauvegardées."
        );
        builder.setPositiveButton("OK", null);
        builder.show();

    }




}