package com.example.super_cep.view.fragments.Enveloppe;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.R;

public class ZoneViewHolder extends RecyclerView.ViewHolder {

    private View root;

    public ZoneViewHolder(@NonNull View root) {
        super(root);
        this.root = root;
    }



    public TextView getZoneName() {
        return root.findViewById(R.id.textViewZoneName);
    }

    public TableLayout getTableLayout(){
        return root.findViewById(R.id.tableLayoutZoneElements);
    }
}
