package fr.sieml.super_cep.view.fragments.UsageEtOccupation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.model.Releve.Calendrier.Calendrier;
import fr.sieml.super_cep.model.Releve.Zone;

public class CalendrierAdaptater extends RecyclerView.Adapter<CalendrierViewHolder> {

    private Calendrier[] calendriers;
    private Zone[] zones;

    private CalendrierViewHolderListener listener;

    public CalendrierAdaptater(Calendrier[] calendriersValues, Zone[] zones, CalendrierViewHolderListener listener){
        this.calendriers = calendriersValues;
        this.zones = zones;
        this.listener = listener;
    }

    public void updateValues(Calendrier[] calendriers, Zone[] zones){
        this.calendriers = calendriers;
        this.zones = zones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendrierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_calendrier, parent, false);
        return new CalendrierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendrierViewHolder holder, int position) {
        holder.bind(calendriers[position], zones, listener);
    }


    @Override
    public int getItemCount() {
        return calendriers.length;
    }
}
