package fr.sieml.super_cep.view.fragments.Preconisation;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.databinding.ViewholderPreconisationTextBinding;

public class PreconisationViewHolderText extends RecyclerView.ViewHolder {

    private ViewholderPreconisationTextBinding binding;

    public PreconisationViewHolderText(View view) {
        super(view);
        binding = ViewholderPreconisationTextBinding.bind(view);
    }


    public void bind(String preconisation, PreconisationViewHolderListener listener){
        binding.editTextTextMultiLine2.setText(preconisation);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPreconisationClicked(preconisation);
            }
        });

        binding.editTextTextMultiLine2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!binding.editTextTextMultiLine2.getText().toString().equals(preconisation))
                    listener.editPreconisation(preconisation, binding.editTextTextMultiLine2.getText().toString());
            }
        });
    }
}
