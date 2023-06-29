package com.example.super_cep.view.fragments.UsageEtOccupation;

import android.annotation.SuppressLint;
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

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentUeoCalendrierBinding;
import com.example.super_cep.databinding.ViewUeoCalendrierJourBinding;
import com.example.super_cep.databinding.ViewUsageEtOccupationHeureBinding;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Calendrier.CalendrierDate;
import com.example.super_cep.model.Releve.Calendrier.ChaufferOccuper;
import com.example.super_cep.model.Releve.Releve;

import org.apache.poi.poifs.property.Child;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentUsageEtOccupationCalendrier extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUeoCalendrierBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        Releve releve = releveViewModel.getReleve().getValue();
        Calendrier calendrier = releve.calendriers.get(nomCalendrier);
        binding.textViewNomCalendrier.setText(calendrier.nom);
        binding.textViewZoneCalendrier.setText(zonesToString(calendrier.zones));

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
                bindingViewHeure.textViewHeur.setText(String.valueOf((int)Math.floor(j)) + "h" + String.valueOf((int)((j % 1) * 60)));
                bindingViewJour.linearLayoutJour.addView(bindingViewHeure.getRoot());

                CalendrierDate calendrierDate = getCalendrierDateFromIndex((int) ((i * 48) + (j * 2)));
                if(map.containsKey(calendrierDate)){
                    ChaufferOccuper chaufferOccuper = map.get(calendrierDate);
                    if(chaufferOccuper == ChaufferOccuper.CHAUFFER_OCCUPER){
                        bindingViewHeure.imageViewLogoPeople.setVisibility(View.VISIBLE);
                        bindingViewHeure.imageViewLogoChauffage.setVisibility(View.VISIBLE);
                    }else if(chaufferOccuper == ChaufferOccuper.CHAUFFER) {
                        bindingViewHeure.imageViewLogoChauffage.setVisibility(View.VISIBLE);
                    }else if(chaufferOccuper == ChaufferOccuper.OCCUPER){
                        bindingViewHeure.imageViewLogoPeople.setVisibility(View.VISIBLE);
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
                            if(bindingViewHeure.imageViewLogoChauffage.getVisibility() == View.VISIBLE){
                                bindingViewHeure.imageViewLogoChauffage.setVisibility(View.INVISIBLE);
                            }else{
                                bindingViewHeure.imageViewLogoChauffage.setVisibility(View.VISIBLE);
                            }
                        }else{
                            if(bindingViewHeure.imageViewLogoPeople.getVisibility() == View.VISIBLE){
                                bindingViewHeure.imageViewLogoPeople.setVisibility(View.INVISIBLE);
                            }else{
                                bindingViewHeure.imageViewLogoPeople.setVisibility(View.VISIBLE);
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
        final Drawable backgroundHighlit = getContext().getDrawable(R.drawable.border_black);
        final Drawable defaultBackground = binding.toggleButtonChauffage.getBackground();
        binding.toggleButtonChauffage.setBackground(backgroundHighlit);
        binding.toggleButtonChauffage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonChauffage.setBackground(backgroundHighlit);
                binding.toggleButtonOccupation.setBackground(defaultBackground);
                modeChauffage = true;

            }
        });

        binding.toggleButtonOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonOccupation.setBackground(backgroundHighlit);
                binding.toggleButtonChauffage.setBackground(defaultBackground);
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
        Map<CalendrierDate, ChaufferOccuper> map = calendrier.calendrierDateChaufferOccuperMap;
        if(map == null){
            map = new HashMap<>();
            calendrier.calendrierDateChaufferOccuperMap = map;
        }

        for (int i = 0; i < childsHeurs.length; i++) {
            View childHeur = childsHeurs[i];
            ViewUsageEtOccupationHeureBinding bindingViewHeure = ViewUsageEtOccupationHeureBinding.bind(childHeur);
            ChaufferOccuper chaufferOccuper;
            if(bindingViewHeure.imageViewLogoChauffage.getVisibility() == View.VISIBLE
                    && bindingViewHeure.imageViewLogoPeople.getVisibility() == View.VISIBLE) {
                chaufferOccuper = ChaufferOccuper.CHAUFFER_OCCUPER;
            }else if(bindingViewHeure.imageViewLogoChauffage.getVisibility() == View.VISIBLE) {
                chaufferOccuper = ChaufferOccuper.CHAUFFER;
            }else if(bindingViewHeure.imageViewLogoPeople.getVisibility() == View.VISIBLE) {
                chaufferOccuper = ChaufferOccuper.OCCUPER;
            }else{
                continue;
            }
            CalendrierDate calendrierDate = getCalendrierDateFromIndex(i);
            map.put(calendrierDate, chaufferOccuper);
        }
        releveViewModel.setReleve(releve);
        Toast.makeText(getContext(), "calendrier sauvegardé", Toast.LENGTH_SHORT).show();
    }
}