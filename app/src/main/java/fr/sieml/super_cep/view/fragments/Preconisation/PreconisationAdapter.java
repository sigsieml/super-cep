package fr.sieml.super_cep.view.fragments.Preconisation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;

public class PreconisationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private String[] preconisations;

    private PreconisationViewHolderListener listener;

    public PreconisationAdapter(String[] preconisations, PreconisationViewHolderListener listener){
        this.preconisations = preconisations;
        this.listener = listener;
    }

    public void update(String[] preconisations){
        this.preconisations = preconisations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_preconisation_photo, parent, false);
            return new PreconisationViewHolderPhoto(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_preconisation_text, parent, false);
            return new PreconisationViewHolderText(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(preconisations[position].contains("content://") || preconisations[position].contains("file://")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PreconisationViewHolderPhoto) {
            ((PreconisationViewHolderPhoto) holder).bind(preconisations[position], listener);
        } else if (holder instanceof PreconisationViewHolderText) {
            ((PreconisationViewHolderText) holder).bind(preconisations[position], listener);
        }else{
            throw new RuntimeException("Unknown view holder type");
        }
    }

    @Override
    public int getItemCount() {
        return preconisations.length;
    }
}
