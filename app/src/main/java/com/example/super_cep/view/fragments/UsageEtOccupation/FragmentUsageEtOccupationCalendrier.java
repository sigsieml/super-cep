package com.example.super_cep.view.fragments.UsageEtOccupation;

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

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentUeoCalendrierBinding;
import com.example.super_cep.databinding.ViewUsageEtOccupationJourBinding;
import com.example.super_cep.model.Releve.Calendrier.Calendrier;
import com.example.super_cep.model.Releve.Releve;

import java.util.List;

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
    private void setupCalendrier(Calendrier calendrier) {

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                Log.i("GESTURE", "onDoubleTap");
                modeAjout = true;
                scrollable = false;
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                Log.i("GESTURE", "onSingleTap");
                modeAjout = false;
                scrollable = true;
                return super.onSingleTapConfirmed(e);
            }

        });

        binding.scroolViewJours.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    modeAjout = false;
                    scrollable = true;
                }

                if (modeAjout == true && (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN )) {
                    Log.i("GESTURE", "onTouch" + modeAjout);
                    float x = event.getRawX();
                    float y = event.getRawY();
                    for (int i = 0; i < binding.linearLayoutJours.getChildCount(); i++) {
                        View child = binding.linearLayoutJours.getChildAt(i);
                        if (isPointInsideView(x, y, child)) {
                            // child est la View touchée, faites ce que vous voulez avec
                            if(modeChauffage){
                                child.setBackground(getResources().getDrawable(R.drawable.border_green));
                            }else{
                                child.setBackground(getResources().getDrawable(R.drawable.border_teal));
                            }
                        }
                    }
                }

                return gestureDetector.onTouchEvent(event) || !scrollable;
            }
        });
        for (float i = 0; i < 24; i += 0.5) {
            ViewUsageEtOccupationJourBinding bindingViewJour = ViewUsageEtOccupationJourBinding.inflate(getLayoutInflater());
            bindingViewJour.textViewHeur.setText(String.valueOf((int)Math.floor(i)) + "h" + String.valueOf((int)((i % 1) * 60)));


            binding.linearLayoutJours.addView(bindingViewJour.getRoot());
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
        binding.toggleButtonChauffage.setChecked(true);
        binding.toggleButtonChauffage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonOccupation.setChecked(!binding.toggleButtonChauffage.isChecked());
                modeChauffage = binding.toggleButtonChauffage.isChecked();
            }
        });

        binding.toggleButtonOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toggleButtonChauffage.setChecked(!binding.toggleButtonOccupation.isChecked());
                modeChauffage = binding.toggleButtonChauffage.isChecked();
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


}