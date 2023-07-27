package com.example.super_cep.view.fragments.Enveloppe;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;
import com.example.super_cep.databinding.ViewZoneBinding;
import com.google.android.flexbox.FlexboxLayout;

public class ZoneViewHolder extends RecyclerView.ViewHolder {

    public View root;
    public ViewZoneBinding binding;
    public ZoneViewHolder(@NonNull View root) {
        super(root);
        this.root = root;
        binding = ViewZoneBinding.bind(root);
    }



    public TextView getZoneName() {
        return root.findViewById(R.id.textViewZoneName);
    }
    public FlexboxLayout getFlexboxLayout(){
        return binding.flexboxLayoutZoneElements;
    }
}
