package com.example.super_cep.view.fragments.Preconisation;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderPreconisationPhotoBinding;

public class PreconisationViewHolderPhoto extends RecyclerView.ViewHolder {

    private ViewholderPreconisationPhotoBinding binding;

    public PreconisationViewHolderPhoto(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderPreconisationPhotoBinding.bind(itemView);
    }

    public void bind(String preconisation, PreconisationViewHolderListener listener) {
        Uri uri = Uri.parse(preconisation);
        binding.imageView4.setImageURI(uri);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPreconisationClicked(preconisation);
            }
        });
    }
}
