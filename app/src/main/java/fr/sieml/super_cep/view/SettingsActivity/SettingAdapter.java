package fr.sieml.super_cep.view.SettingsActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;

import java.util.List;
import java.util.Map;

public class SettingAdapter extends RecyclerView.Adapter<SettingViewHolder> {
    private Map<String, List<String>> configData;
    private SettingViewHolderListener listener;

    public SettingAdapter(Map<String, List<String>> configData, SettingViewHolderListener listener) {
        this.configData = configData;
        this.listener = listener;
    }

    public void update(Map<String, List<String>> configData) {
        this.configData = configData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_setting, parent, false);
        return new SettingViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) configData.entrySet().toArray()[position];
        holder.setItemName(entry.getKey());
        holder.setValues(entry.getValue());

    }

    @Override
    public int getItemCount() {
        return configData.keySet().size();
    }
}
