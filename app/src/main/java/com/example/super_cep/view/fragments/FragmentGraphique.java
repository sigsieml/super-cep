package com.example.super_cep.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.super_cep.R;
import com.example.super_cep.controller.Conso.ConsoConfigViewModel;
import com.example.super_cep.controller.Conso.ConsoProvider;
import com.example.super_cep.controller.Conso.ConsoProviderListener;
import com.example.super_cep.controller.Conso.Energie;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.FragmentGraphiqueBinding;
import com.example.super_cep.controller.Conso.Anner;
import com.example.super_cep.controller.Conso.ConsoParser;
import com.example.super_cep.databinding.ItemEnergieBinding;
import com.example.super_cep.view.AideFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.flexbox.FlexboxLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentGraphique extends Fragment implements AideFragment {

    private FragmentGraphiqueBinding binding;

    private String defaultValueNomBatimentConso;
    private ConsoConfigViewModel consoConfigViewModel;
    private ConsoParser consoParser;
    private ConsoProvider consoProvider;
    private ReleveViewModel releveViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGraphiqueBinding.inflate(inflater,container,false);
        consoConfigViewModel = new ViewModelProvider(requireActivity()).get(ConsoConfigViewModel.class);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        setupFab();
        setUpSurface();
        loadConsoParser();
        if(consoParser == null) return binding.getRoot();
        setupSpinner();
        if(consoConfigViewModel.getNomBatimentConso() != null){
            consoAlreadyExist(consoConfigViewModel.getNomBatimentConso());
        }
        return binding.getRoot();
    }

    private void setUpSurface() {
        float surfaceTotalChauffe = releveViewModel.getReleve().getValue().surfaceTotaleChauffe;
        binding.editTextNumberSurfaceBatiment.setText(String.valueOf(surfaceTotalChauffe));
        binding.editTextNumberSurfaceBatiment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                releveViewModel.setSurfaceTotaleChauffe(s.toString().isEmpty() ? 0 : Float.parseFloat(s.toString()));
                if(binding.autoCompleteNomBatiment.getText().toString().equals(defaultValueNomBatimentConso) ||
                        binding.autoCompleteNomBatiment.getText().toString().isEmpty()) return;
                String nomBatiment = binding.autoCompleteNomBatiment.getText().toString();
                updateDataTable(nomBatiment);
                updateRatioTable(nomBatiment);
            }

        });

         binding.editTextPrcBatiment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty() || s.toString().equals("0")) return;
                consoConfigViewModel.setPourcentageBatiment(Float.parseFloat(s.toString()));
                String nomBatiment = binding.autoCompleteNomBatiment.getText().toString();
                updateDataTable(nomBatiment);
                updateRatioTable(nomBatiment);
            }
        });

    }


    private void loadConsoParser() {
        consoProvider = new ConsoProvider(this, new ConsoProviderListener() {
            @Override
            public void onConsoParserChanged(ConsoParser consoParser) {
                FragmentGraphique.this.consoParser = consoParser;
                consoConfigViewModel.setConsoParser(consoParser);
                setupSpinner();
            }
        });
        if(consoConfigViewModel.getConsoParser().getValue() == null){
            consoParser = consoProvider.getConsoParser();
            if(consoParser == null) return;
            consoConfigViewModel.setConsoParser(consoParser);
        }else{
            consoParser = consoConfigViewModel.getConsoParser().getValue();
        }
    }

    private void setupFab() {
        binding.fabLoadConsoFile.setOnClickListener((view) -> {
            //request .xlsx file
            consoProvider.loadNewConso();
        } );

        binding.fabEditFile.setOnClickListener((view) -> {
            //open file
            consoProvider.editFileInExcel();
        } );
    }


    private void setupSpinner() {
        List<String> values = consoParser.getBatiments();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, values);
        binding.autoCompleteNomBatiment.setAdapter(adapter);

        binding.autoCompleteNomBatiment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBatimentSelected(parent.getItemAtPosition(position).toString());
                updateRatioTable(parent.getItemAtPosition(position).toString());
                // remove focus
                binding.autoCompleteNomBatiment.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.autoCompleteNomBatiment.getWindowToken(), 0);
            }
        });

        binding.autoCompleteNomBatiment.setOnTouchListener((v, event) -> {
            //only if it's a click
            if (event.getAction() != android.view.MotionEvent.ACTION_UP)
                return false;
            binding.autoCompleteNomBatiment.showDropDown();
            return false;
        });



        defaultValueNomBatimentConso = binding.autoCompleteNomBatiment.getText().toString();
        binding.spinnerMeilleurAnne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                consoConfigViewModel.setMeilleurAnne(binding.spinnerMeilleurAnne.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void onBatimentSelected(String nomBatiment){
        if(nomBatiment.equals(consoConfigViewModel.getNomBatimentConso())) return;
        List<String> anne = consoParser.getAnneOfBatiment(nomBatiment);
        consoConfigViewModel.setNomBatimentConso(nomBatiment);
        binding.linearLayoutAnne.removeAllViews();
        for(String s : anne){
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSelectedYears();
                updateDataTable(nomBatiment);
                updateRatioTable(nomBatiment);
            });
            checkBox.setText(s);
            checkBox.setChecked(true);
            binding.linearLayoutAnne.addView(checkBox);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, anne);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMeilleurAnne.setAdapter(adapter);
        if(anne.size() > 0){
            consoConfigViewModel.setMeilleurAnne(anne.get(anne.size()-1));
            setSpinnerSelection(binding.spinnerMeilleurAnne, anne.get(anne.size()-1));
        }
        updateDataTable(nomBatiment);
    }
    private void consoAlreadyExist(String nomBatiment){

        List<String> anne = consoParser.getAnneOfBatiment(nomBatiment);
        binding.linearLayoutAnne.removeAllViews();
        for(String s : anne){
            CheckBox checkBox = new CheckBox(getContext());
            if(consoConfigViewModel.getAnneesConso() != null && consoConfigViewModel.getAnneesConso().contains(s)){
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSelectedYears();
            });
            checkBox.setText(s);
            binding.linearLayoutAnne.addView(checkBox);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, anne);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMeilleurAnne.setAdapter(adapter);
        if(consoConfigViewModel.getMeilleurAnne() != null){
            setSpinnerSelection(binding.spinnerMeilleurAnne, consoConfigViewModel.getMeilleurAnne());
        }
        updateDataTable(nomBatiment);
    }

    public void updateSelectedYears(){
        List<String> years = getSelectedYears();
        consoConfigViewModel.setAnneesConso(years);
    }
    public List<String> getSelectedYears(){
        List<String> years = new ArrayList<>();
        for(int i = 0; i < binding.linearLayoutAnne.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) binding.linearLayoutAnne.getChildAt(i);
            if(checkBox.isChecked()){
                years.add(checkBox.getText().toString());
            }
        }
        return years;
    }

    public void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void updateDataTable(String nomBatiment){
        if(consoParser == null) return;
        List<String> annees = getSelectedYears();
        List<Anner> anneesWatt = consoParser.getConsoWatt(nomBatiment, annees);
        applyPrctageBatiment(anneesWatt);
        List<Anner> anneesEuro = consoParser.getConsoEuro(nomBatiment, annees);
        applyPrctageBatiment(anneesEuro);
        addChart(binding.chartWh, annees, anneesWatt, "kWh");
        addChart(binding.chartEuro, annees, anneesEuro, "€");

        FlexboxLayout flexboxLayout = binding.flexboxLayout;
        flexboxLayout.removeAllViews();
        for (int i=0; i<Energie.ENERGIES.length; i++) {
            ItemEnergieBinding itemEnergieBinding = ItemEnergieBinding.bind(LayoutInflater.from(getContext()).inflate(R.layout.item_energie, flexboxLayout, false));
            itemEnergieBinding.textViewItemName.setText(Energie.ENERGIES[i]);
            itemEnergieBinding.viewColor.setBackgroundColor(Energie.COLORS[i].getRGB());
            flexboxLayout.addView(itemEnergieBinding.getRoot());
        }
    }

    private void applyPrctageBatiment(List<Anner> anners){
        float prctage = binding.editTextPrcBatiment.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextPrcBatiment.getText().toString());
        if(prctage == 0) return;
        for(Anner anner : anners){
            for (Energie energie : anner.energies.keySet()){
               anner.energies.put(energie, anner.energies.get(energie) * prctage / 100);
            }
        }
    }

    private void addChart(BarChart chart, List<String> annees, List<Anner> annesEnergie, String suffix){

        chart.setPinchZoom(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawValueAboveBar(true); // make sure it's set to true
        chart.setHighlightFullBarEnabled(false);


        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i=0; i<annees.size(); i++) {
            Anner anner = annesEnergie.get(i);
            Energie[] energies = Energie.values();
            float[] energieValues = new float[energies.length];
            for (int j=0; j<energies.length; j++) {
                energieValues[j] = (float)anner.getEnergie(energies[j]);
            }
            values.add(new BarEntry(i, energieValues));
        }

        BarDataSet set1 = new BarDataSet(values, "Consommation par énergie");
        set1.setDrawIcons(false);
        set1.setDrawValues(true);
        List<Integer> colors = new ArrayList<>();
        for (java.awt.Color color : Energie.COLORS) {
            int colorInt = color.getRGB();
            colors.add(colorInt);
        }
        set1.setColors(colors);
        set1.setStackLabels(Energie.ENERGIES);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new StackedValueFormatter(false, suffix, 0));


        chart.setData(data);
        chart.setFitBars(true);
        chart.invalidate();

        // Setting X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(annees));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);


    }


    public void updateRatioTable(String nomBatiment){
        if(consoParser == null) return;
        Drawable drawableBackground = getResources().getDrawable(R.drawable.border_black_thin);
        binding.linearlayoutConso.removeAllViews();
        List<String> annees =  getSelectedYears();
        List<Anner> anneesWatt = consoConfigViewModel.applyPourcentageToConso(consoParser.getConsoWatt(nomBatiment, annees));
        List<Anner> annesEuro = consoConfigViewModel.applyPourcentageToConso(consoParser.getConsoEuro(nomBatiment, annees));

        float surface = releveViewModel.getReleve().getValue().surfaceTotaleChauffe;
        if(surface == 0) {
            binding.surfaceNotSetLayout.setVisibility(View.VISIBLE);
            return;
        }else{
            binding.surfaceNotSetLayout.setVisibility(View.GONE);
        }
        //Annes
        LinearLayout linearLayoutAnnees = new LinearLayout(getContext());
        linearLayoutAnnees.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTitre = new TextView(getContext());
        textViewTitre.setText("kWh / m²");
        linearLayoutAnnees.addView(textViewTitre);
        for(String anne : annees){
            TextView textViewAnne = new TextView(getContext());
            int widthInDp = 50;
            textViewAnne.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
            textViewAnne.setText(anne);
            textViewAnne.setBackground(drawableBackground);
            linearLayoutAnnees.addView(textViewAnne);
        }
        TextView titreEuro = new TextView(getContext());
        titreEuro.setText("€ / m²");
        linearLayoutAnnees.addView(titreEuro);
        for(String anne : annees){
            TextView textViewAnne = new TextView(getContext());
            textViewAnne.setText(anne);
            int widthInDp = 50;
            textViewAnne.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
            textViewAnne.setBackground(drawableBackground);
            linearLayoutAnnees.addView(textViewAnne);
        }
        binding.linearlayoutConso.addView(linearLayoutAnnees);


        // Total
        LinearLayout linearLayoutTotal = new LinearLayout(getContext());
        linearLayoutTotal.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTotal = new TextView(getContext());
        textViewTotal.setText("Total");
        linearLayoutTotal.addView(textViewTotal);
        for(Anner anner : anneesWatt){
            TextView textViewAnne = new TextView(getContext());
            int widthInDp = 60;
            textViewAnne.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
            textViewAnne.setBackground(drawableBackground);
            String value =  (int)(anner.total / surface) + "";
            textViewAnne.setText(value);
            linearLayoutTotal.addView(textViewAnne);
        }
        TextView textViewTotalEuro = new TextView(getContext());
        textViewTotalEuro.setText("");
        linearLayoutTotal.addView(textViewTotalEuro);
        for(Anner anner : annesEuro){
            TextView textViewAnne = new TextView(getContext());
            int widthInDp = 60;
            textViewAnne.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
            textViewAnne.setBackground(drawableBackground);
            String value = (int)( anner.total / surface) + "";
            textViewAnne.setText(value);
            linearLayoutTotal.addView(textViewAnne);
        }
        binding.linearlayoutConso.addView(linearLayoutTotal);

        //Energie
        for(Energie energie : Energie.values()){
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(getContext());
            textView.setText(energie.nomEnergie);
            linearLayout.addView(textView);
            for(Anner anner : anneesWatt){
                TextView textViewAnne = new TextView(getContext());
                int widthInDp = 120;
                textView.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
                textViewAnne.setBackground(drawableBackground);
                String value = (int)(anner.getEnergie(energie) / surface) + "";
                textViewAnne.setText(value);
                linearLayout.addView(textViewAnne);
            }
            TextView textViewEuro = new TextView(getContext());
            textViewEuro.setText("");
            linearLayout.addView(textViewEuro);
            for(Anner anner: annesEuro){
                TextView textViewAnne = new TextView(getContext());
                int widthInDp = 120;
                textView.setWidth( (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics()));
                textViewAnne.setBackground(drawableBackground);
                String value = (int)(anner.getEnergie(energie) / surface) + "";
                textViewAnne.setText(value);
                linearLayout.addView(textViewAnne);
            }
            binding.linearlayoutConso.addView(linearLayout);

        }

    }


    public class StackedValueFormatter extends ValueFormatter
    {

        /**
         * if true, all stack values of the stacked bar entry are drawn, else only top
         */
        private boolean mDrawWholeStack;

        /**
         * a string that should be appended behind the value
         */
        private String mSuffix;

        private DecimalFormat mFormat;

        /**
         * Constructor.
         *
         * @param drawWholeStack if true, all stack values of the stacked bar entry are drawn, else only top
         * @param suffix         a string that should be appended behind the value
         * @param decimals       the number of decimal digits to use
         */
        public StackedValueFormatter(boolean drawWholeStack, String suffix, int decimals) {
            this.mDrawWholeStack = drawWholeStack;
            this.mSuffix = suffix;
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < decimals; i++) {
                if (i == 0)
                    b.append(".");
                b.append("0");
            }

            this.mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
        }

        @Override
        public String getBarStackedLabel(float value, BarEntry entry) {

            float[] vals = entry.getYVals();

            if (vals != null) {

                // find out if we are on top of the stack
                if (vals[vals.length - 1] == value) {

                    // return the "sum" across all stack values
                    return mFormat.format(entry.getY());
                }
            }
            return mFormat.format(value) + mSuffix; // return empty
        }
    }



    @Override
    public void aide() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aide graphiques de consommation d'énergie");
        builder.setMessage("Chargement des données de consommation :\n\n" +
        "Cliquez sur le bouton flottant avec un logo de téléchargement situé en bas à droite de l'écran, puis sélectionnez et chargez un fichier .xlsx contenant les données de consommation.\n\n" +
        "Modification des données de consommation :\n\n" +
        "Vous pouvez manuellement modifier les données de consommation en les ouvrant dans Excel en cliquant sur le deuxième bouton flottant avec un logo en forme de crayon en bas à droite de l'écran. Cette fonctionnalité nécessite d'avoir installé l'application Android Excel, disponible sur le Play Store.\n\n" +
        "Surface totale du bâtiment :\n\n" +
        "La surface chauffée du bâtiment est utilisée pour calculer les ratios Kwh/m² et €/m². Elle doit être différente de 0 pour que le tableau s'affiche. La surface chauffée peut également être modifiée dans la partie \"Bâtiment\".\n\n" +
        "Nom du bâtiment :\n\n" +
        "Utilisez le menu déroulant pour choisir le bâtiment dont vous voulez visualiser la consommation. Vous pouvez aussi rechercher le bâtiment en tapant son nom.\n\n" +
        "Pourcentage du bâtiment :\n\n" +
        "Cette valeur est utilisée pour le calcul des Kwh/m² et €/m². Par exemple, si la consommation du bâtiment ne représente que 30 % de la consommation totale des graphiques, il suffit de changer la valeur à \"30\", et les valeurs de consommation seront multipliées par 30 %.\n\n" +
        "Année de calcul des ratios :\n\n" +
        "Dans le PowerPoint, les ratios Kwh/m² et €/m² ne sont affichés que pour une seule année. Cette année peut être sélectionnée ici.\n\n" +
        "Sélection de l'année ou des années :\n\n" +
        "Une fois le bâtiment sélectionné, une liste de cases avec les années disponibles apparaîtra. Cochez les cases correspondant aux années que vous souhaitez visualiser et faire apparaître dans le PowerPoint. Vous pouvez choisir une ou plusieurs années."
        );
        builder.setPositiveButton("Merci, j'ai compris !", null);
        builder.show();
    }
}