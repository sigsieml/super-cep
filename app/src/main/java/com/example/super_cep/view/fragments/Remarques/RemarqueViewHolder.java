package com.example.super_cep.view.fragments.Remarques;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.super_cep.databinding.ViewholderRemarqueBinding;
import com.example.super_cep.model.Releve.Remarque;


interface RemarqueViewHolderListener{
    void onClick(Remarque remarque);
    void onRemarqueEdited(String oldNom, Remarque remarque);
}
public class RemarqueViewHolder extends RecyclerView.ViewHolder {

    private ViewholderRemarqueBinding binding;

    private EditText editTextTitleRemarque;
    public RemarqueViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ViewholderRemarqueBinding.bind(itemView);
    }

    public void bind(Remarque remarque, RemarqueViewHolderListener listener){
        //if (remarque.personalise) replace textViewTitleRemarque with editTextTitleRemarque
        if(remarque.personalise){
            if(editTextTitleRemarque == null){
                editTextTitleRemarque = new EditText(binding.getRoot().getContext());
                editTextTitleRemarque.setTextSize(24);
                editTextTitleRemarque.setLayoutParams(binding.textViewTitleRemarque.getLayoutParams());
                binding.textViewTitleRemarque.setVisibility(View.GONE);
                binding.getRoot().addView(editTextTitleRemarque);
            }
            editTextTitleRemarque.setText(remarque.nom);
        }else{
            if(editTextTitleRemarque != null){
                binding.getRoot().removeView(editTextTitleRemarque);
                editTextTitleRemarque = null;
                binding.textViewTitleRemarque.setVisibility(View.VISIBLE);
            }
            binding.textViewTitleRemarque.setText(remarque.nom);
        }
        binding.editTextTextMultiLine.setText(remarque.description);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(remarque);
            }
        });

        binding.editTextTextMultiLine.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                saveRemarque(remarque, listener);
            }
        });

        if(editTextTitleRemarque != null)
            editTextTitleRemarque.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    saveRemarque(remarque, listener);
                }
            });

    }

    private void saveRemarque(Remarque remarque, RemarqueViewHolderListener listener) {
        if(remarque.description.equals(binding.editTextTextMultiLine.getText().toString())&&
                (!remarque.personalise || remarque.nom.equals(editTextTitleRemarque.getText().toString())))
            return;

        try {
            listener.onRemarqueEdited(remarque.nom,
                    new Remarque(remarque.personalise ? editTextTitleRemarque.getText().toString() : remarque.nom,
                            binding.editTextTextMultiLine.getText().toString(),
                            remarque.personalise));
        }catch (Exception e){
            Log.e("RemarqueViewHolder", "onFocusChange: ", e);
            Toast.makeText(binding.getRoot().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
