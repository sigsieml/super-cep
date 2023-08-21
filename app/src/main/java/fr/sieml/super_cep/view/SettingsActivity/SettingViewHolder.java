package fr.sieml.super_cep.view.SettingsActivity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.databinding.ViewholderSettingBinding;

import java.util.List;


interface SettingViewHolderListener {
    void onSettingClicked(String key);
}
public class SettingViewHolder extends RecyclerView.ViewHolder {

    ViewholderSettingBinding binding;

    public SettingViewHolder(@NonNull View itemView, SettingViewHolderListener listener) {
        super(itemView);
        binding = ViewholderSettingBinding.bind(itemView);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSettingClicked(binding.textViewItemName.getText().toString());
            }
        });
    }

    public void setItemName(String key) {
        binding.textViewItemName.setText(key);
    }

    public void setValues(List<String> value) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : value) {
            stringBuilder.append(s).append(",");
        }
        binding.textViewValues.setText(stringBuilder.toString());
    }
}
