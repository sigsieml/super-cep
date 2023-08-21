package fr.sieml.super_cep.view.MainActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;


public class ReleveRecentAdapter extends RecyclerView.Adapter<ReleveRecentViewHolder> {

    private String[] releves;
    private ReleveRecentViewHolderListener listener;

    public ReleveRecentAdapter(String[] releves, ReleveRecentViewHolderListener listener) {
        this.releves = releves;
        this.listener = listener;
    }


    public void updateReleves(String[] releves) {
        this.releves = releves;
        notifyDataSetChanged();
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
