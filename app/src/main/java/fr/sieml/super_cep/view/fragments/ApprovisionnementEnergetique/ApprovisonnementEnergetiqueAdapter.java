package fr.sieml.super_cep.view.fragments.ApprovisionnementEnergetique;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.model.Releve.ApprovionnementEnergetique.ApprovisionnementEnergetique;

public class ApprovisonnementEnergetiqueAdapter extends RecyclerView.Adapter<ApprovisionnementEnergetiqueViewHolder> {

    private ApprovisionnementEnergetique[] ecs;
    private ApprovisionnementEnergetiqueViewHolderListener listener;

    public ApprovisonnementEnergetiqueAdapter(ApprovisionnementEnergetique[] ecs, ApprovisionnementEnergetiqueViewHolderListener listener) {
        this.ecs = ecs;
        this.listener = listener;
    }

    public void update(ApprovisionnementEnergetique[] ecs) {
        this.ecs = ecs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApprovisionnementEnergetiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_approvisionnement_energetique, parent, false);
        return new ApprovisionnementEnergetiqueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovisionnementEnergetiqueViewHolder holder, int position) {
        holder.bind(ecs[position], listener);
    }

    @Override
    public int getItemCount() {
        return ecs.length;
    }
}
