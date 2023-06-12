package com.example.super_cep.view.fragments.Enveloppe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.R;
import com.example.super_cep.databinding.FragmentEnveloppeBinding;
import com.example.super_cep.model.Zone;
import com.example.super_cep.model.ZoneElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Enveloppe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Enveloppe extends Fragment {

    public List<Zone> zones = new ArrayList<>();

    public FragmentEnveloppeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<ZoneElement> zoneElements = new ArrayList<>();
        zoneElements.add(new ZoneElement("mur chaufferie"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zoneElements.add(new ZoneElement("mur salon"));
        zones.add(new Zone("Zone 1", zoneElements));
        zones.add(new Zone("Zone 2",zoneElements));
        zones.add(new Zone("Zone 3", zoneElements));


        binding = FragmentEnveloppeBinding.inflate(inflater, container, false);
        RecyclerView recyclerView = binding.RecyclerViewZones;
        recyclerView.setAdapter(new ZonesAdaptater(zones));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return binding.getRoot();
    }


}