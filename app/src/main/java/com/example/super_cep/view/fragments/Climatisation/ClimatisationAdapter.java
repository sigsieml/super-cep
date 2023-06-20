package com.example.super_cep.view.fragments.Climatisation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Climatisation;
import com.example.super_cep.view.fragments.Chauffages.ChauffageViewHolder;

public class ClimatisationAdapter extends RecyclerView.Adapter<ClimatisationViewHolder> {

    private Climatisation[] climatisations;
    private ClimatisationViewHolderListener listener;

    public ClimatisationAdapter(Climatisation[] climatisations, ClimatisationViewHolderListener listener) {
        this.climatisations = climatisations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClimatisationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_climatisation, parent, false);
        return new ClimatisationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClimatisationViewHolder holder, int position) {
        holder.bind(climatisations[position], listener);
    }

    @Override
    public int getItemCount() {
        return climatisations.length;
    }

    public void update(Climatisation[] climatisations) {
        this.climatisations = climatisations;
        notifyDataSetChanged();
    }
}
