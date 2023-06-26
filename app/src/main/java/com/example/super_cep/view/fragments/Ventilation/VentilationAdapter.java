package com.example.super_cep.view.fragments.Ventilation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Releve.Ventilation;

public class VentilationAdapter extends RecyclerView.Adapter<VentilationViewHolder> {

    private Ventilation[] ventilations;
    private VentilationViewHolderListener listener;

    public VentilationAdapter(Ventilation[] ventilations, VentilationViewHolderListener listener) {
        this.ventilations = ventilations;
        this.listener = listener;
    }

    public void update(Ventilation[] ventilations) {
        this.ventilations = ventilations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VentilationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ventilation, parent, false);
        return new VentilationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentilationViewHolder holder, int position) {
        holder.bind(ventilations[position], listener);
    }

    @Override
    public int getItemCount() {
        return ventilations.length;
    }
}
