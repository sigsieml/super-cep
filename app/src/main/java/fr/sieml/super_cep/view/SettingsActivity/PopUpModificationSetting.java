package fr.sieml.super_cep.view.SettingsActivity;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.sieml.super_cep.R;
import fr.sieml.super_cep.databinding.PopupSettingBinding;
import fr.sieml.super_cep.databinding.ViewholderSettingItemBinding;

import java.util.List;

interface PopUpModificationSettingListener {
    void onValidate(String key, List<String> values);
}

public class PopUpModificationSetting extends View {

    public static void create(Context context, String key, List<String> values, PopUpModificationSettingListener listener){
        new PopUpModificationSetting(context, key, values, listener);
    }

    private String key;
    private List<String> values;

    private PopupSettingBinding binding;
    private PopupWindow pw;
    private PopUpModificationSettingListener listener;

    private PopUpModificationSetting(Context context, String key, List<String> values, PopUpModificationSettingListener listener) {
        super(context);
        this.key= key;
        this.values = values;
        this.listener = listener;
        this.binding = PopupSettingBinding.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        pw = new PopupWindow(binding.getRoot(), ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        binding.textView6.setText(key);

        setupRecyclerView();

        binding.buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        binding.buttonSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onValidate(key, values);
                pw.dismiss();
            }
        });

        binding.buttonAjoutSettingItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                values.add(binding.editTextNewSettingItem.getText().toString());
                binding.recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerView.setAdapter(new SettingItemAdapter(values));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private class SettingItemAdapter extends RecyclerView.Adapter<SettingItemViewHolder>{

        private List<String> values;

        public SettingItemAdapter(List<String> values) {
            this.values = values;
        }



        @NonNull
        @Override
        public SettingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_setting_item, parent, false);
            return new SettingItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SettingItemViewHolder holder,int position) {
            holder.binding.editTextSettingItem.setText(values.get(position));
            holder.binding.buttonDeleteSettingItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    values.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            holder.binding.editTextSettingItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if(values.size() > holder.getAdapterPosition())
                        values.set(holder.getAdapterPosition(), s.toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

    private class SettingItemViewHolder extends RecyclerView.ViewHolder{

        public ViewholderSettingItemBinding binding;
        public SettingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderSettingItemBinding.bind(itemView);
        }

    }
}
