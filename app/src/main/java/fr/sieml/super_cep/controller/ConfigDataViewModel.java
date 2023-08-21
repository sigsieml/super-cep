package fr.sieml.super_cep.controller;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import fr.sieml.super_cep.model.ConfigData.ConfigData;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

public class ConfigDataViewModel extends ViewModel {


    MutableLiveData<Map<String, List<String>>> spinnerData;


    public ConfigDataViewModel() {
        spinnerData = new MutableLiveData<>();
    }

    public void setSpinnerData(Map<String, List<String>> spinnerData) {
        this.spinnerData.setValue(spinnerData);
    }


    public MutableLiveData<Map<String, List<String>>> getSpinnerData() {
        return spinnerData;
    }

    public void updateSpinnerData(Spinner spinner, String key) {
        if (!spinnerData.getValue().containsKey(key)) {
            Snackbar.make(spinner, "Erreur lors de la récupération des données de " + key, Snackbar.LENGTH_LONG).show();
            return;
        }
        updateSpinnerData(spinner, spinnerData.getValue().get(key));
    }

    public void setAutoComplete(AutoCompleteTextView multiLineAutoCompleteTextView, String key) {
        if (!spinnerData.getValue().containsKey(key)) {
            Snackbar.make(multiLineAutoCompleteTextView, "Erreur lors de la récupération des données de " + key, Snackbar.LENGTH_LONG).show();
            return;
        }
        setAutoComplete(multiLineAutoCompleteTextView,key, spinnerData.getValue().get(key));
    }

    public void setAutoComplete(AutoCompleteTextView autoCompleteTextView,String key, List<String> strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(autoCompleteTextView.getContext(), android.R.layout.simple_dropdown_item_1line, strings) {
            @NonNull
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = strings;
                        filterResults.count = strings.size();
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }
                };
            }
        };

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnTouchListener((v, event) -> {
            //only if it's a click
            if (event.getAction() != android.view.MotionEvent.ACTION_UP)
                return false;
            autoCompleteTextView.showDropDown();
            return false;
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                // Si l'utilisateur sélectionne l'option d'ajout, ajoutez le texte à la liste
                if (parent.getItemAtPosition(position).toString().equals("Ajouter à la liste")) {
                    String text = autoCompleteTextView.getText().toString();
                    Log.i("test", text);
                    // Ajoutez le texte à la liste s'il n'y est pas déjà
                    AlertDialog.Builder builder = new AlertDialog.Builder(autoCompleteTextView.getContext());
                    builder.setTitle("Ajouter à la liste");
                    builder.setMessage("Voulez-vous ajouter \"" + text + "\" à la liste ?");
                    builder.setPositiveButton("Oui", (dialog, which) -> {
                        strings.add(text);
                        strings.remove("Ajouter à la liste");
                        addToList(autoCompleteTextView, key, strings);
                    });
                    builder.setNegativeButton("Non", (dialog, which) -> {
                    });
                    builder.show();


                }
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            String lastText = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Ajustez la liste des options en fonction du texte entré par l'utilisateur
                if (!strings.contains(s.toString())) {
                    if (!strings.contains("Ajouter à la liste")) {
                        strings.add("Ajouter à la liste");
                        Log.i("test", "add");
                    }
                } else {
                    strings.remove("Ajouter à la liste");
                }
                adapter.notifyDataSetChanged();
                if(s.toString().equals("Ajouter à la liste")){
                    autoCompleteTextView.setText(lastText);
                }
                lastText = s.toString();
            }
        });

    }

    private void addToList(AutoCompleteTextView autoCompleteTextView, String key, List<String> strings) {
        spinnerData.getValue().put(key,  strings);
        setAutoComplete(autoCompleteTextView, key, strings);
        new ConfigDataProvider(autoCompleteTextView.getContext()).saveConfigData(new ConfigData(spinnerData.getValue()));
    }

    public static void updateSpinnerData(Spinner spinner, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}
