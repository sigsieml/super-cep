package fr.sieml.super_cep.view.fragments.ECS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.model.Releve.ECS;

public class ECSAdapter extends RecyclerView.Adapter<ECSViewHolder> {
    private ECS[] ecs;
    private ECSViewHolderListener listener;

    public ECSAdapter(ECS[] ecs, ECSViewHolderListener listener) {
        this.ecs = ecs;
        this.listener = listener;
    }

    public void update(ECS[] ecs) {
        this.ecs = ecs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ECSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ecs, parent, false);
        return new ECSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ECSViewHolder holder, int position) {
        holder.bind(ecs[position], listener);
    }

    @Override
    public int getItemCount() {
        return ecs.length;
    }
}
