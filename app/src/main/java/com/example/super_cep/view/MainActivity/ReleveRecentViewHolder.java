package com.example.super_cep.view.MainActivity;

import android.app.AlertDialog;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderReleveFileBinding;
import com.example.super_cep.model.Export.JsonExporter;
import com.example.super_cep.model.Releve;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

interface ReleveRecentViewHolderListener {
    void onReleveFileClicked(String relevePath);
    void onReleveFileLongClicked(String relevePath);

}

public class ReleveRecentViewHolder extends RecyclerView.ViewHolder {

    ViewholderReleveFileBinding viewholderReleveFileBinding;
    private ReleveRecentViewHolderListener listener;

    public ReleveRecentViewHolder(@NonNull View itemView, ReleveRecentViewHolderListener listener) {
        super(itemView);
        viewholderReleveFileBinding = ViewholderReleveFileBinding.bind(itemView);
        this.listener = listener;
    }

    public void setFileName(String relevePath) {

        viewholderReleveFileBinding.textViewReleveFile.setText(relevePath.replace(".json", ""));
        viewholderReleveFileBinding.textViewReleveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read file relevePath
                listener.onReleveFileClicked(relevePath);
            }
        });

        viewholderReleveFileBinding.textViewReleveFile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //delete file with Dialog
                listener.onReleveFileLongClicked(relevePath);
                return true;
            }
        });
    }
}
