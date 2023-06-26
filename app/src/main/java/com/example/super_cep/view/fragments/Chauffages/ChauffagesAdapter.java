package com.example.super_cep.view.fragments.Chauffages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Releve.Chauffage.Chauffage;

public class ChauffagesAdapter extends RecyclerView.Adapter<ChauffageViewHolder> {

    private Chauffage[] chauffages;
    private ChauffageViewHolderListener listener;

    public ChauffagesAdapter(Chauffage[] chauffages, ChauffageViewHolderListener listener) {
        this.chauffages = chauffages;
        this.listener = listener;
    }

    public void update(Chauffage[] chauffages) {
        this.chauffages = chauffages;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ChauffageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_chauffage, parent, false);
        return new ChauffageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChauffageViewHolder holder, int position) {
        holder.bind(chauffages[position], listener);
    }

    @Override
    public int getItemCount() {
        return chauffages.length;
    }
}
