package com.example.super_cep.view.fragments.Remarques;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Releve.Remarque;

public class RemarqueAdapter  extends RecyclerView.Adapter<RemarqueViewHolder>{

    private Remarque[] remarques;
    private RemarqueViewHolderListener listener;

    public RemarqueAdapter(Remarque[] remarques, RemarqueViewHolderListener listener) {
        this.remarques = remarques;
        this.listener = listener;
    }


    public void update(Remarque[] remarques) {
        this.remarques = remarques;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RemarqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View zoneViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_remarque, parent, false);
        return new RemarqueViewHolder(zoneViewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull RemarqueViewHolder holder, int position) {
        holder.bind(remarques[position], listener);
    }

    @Override
    public int getItemCount() {
        return remarques.length;
    }

}
