package com.example.super_cep.view.MainActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.model.Releve;

import java.util.List;



public class ReleveRecentAdapter extends RecyclerView.Adapter<ReleveRecentViewHolder> {

    private String[] releves;
    private ReleveRecentViewHolderListener listener;

    public ReleveRecentAdapter(String[] releves, ReleveRecentViewHolderListener listener) {
        this.releves = releves;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReleveRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_releve_file, parent, false);
        return new ReleveRecentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReleveRecentViewHolder holder, int position) {
        holder.setFileName(releves[position]);
    }

    @Override
    public int getItemCount() {
        return releves.length;
    }
}
