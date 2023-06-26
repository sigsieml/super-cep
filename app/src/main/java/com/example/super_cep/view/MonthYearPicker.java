package com.example.super_cep.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.super_cep.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class MonthYearPicker extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private Calendar cal = Calendar.getInstance();

    public static final String MONTH_KEY = "monthValue";
    public static final String YEAR_KEY = "yearValue";

    int monthVal = -1  , yearVal =-1 ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        if(extras != null){
            monthVal = extras.getInt(MONTH_KEY , -1);
            yearVal = extras.getInt(YEAR_KEY , -1);
        }
    }

    public static MonthYearPicker newInstance(int monthIndex , int daysIndex , int yearIndex) {
        MonthYearPicker f = new MonthYearPicker();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(MONTH_KEY, monthIndex);
        args.putInt(YEAR_KEY, yearIndex);
        f.setArguments(args);

        return f;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.popup_month_year_dialog, null);
        monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);


        if(monthVal != -1)// && (monthVal > 0 && monthVal < 13))
            monthPicker.setValue(monthVal);
        else
            monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        monthPicker.setDisplayedValues(new DateFormatSymbols().getMonths());


        int maxYear = cal.get(Calendar.YEAR) + 1;
        final int minYear = 1916;
        int arraySize = maxYear - minYear;

        String[] tempArray = new String[arraySize];
        tempArray[0] = "---";
        int tempYear = minYear+1;

        for(int i=0 ; i < arraySize; i++){
            if(i != 0){
                tempArray[i] = " " + tempYear + "";
            }
            tempYear++;
        }
        Log.i("", "onCreateDialog: " + tempArray.length);
        yearPicker.setMinValue(minYear+1);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setDisplayedValues(tempArray);

        if(yearVal != -1)
            yearPicker.setValue(yearVal);
        else
            yearPicker.setValue(tempYear -1);


        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int year = yearPicker.getValue();
                        if(year == (minYear+1)){
                            year = 1904;
                        }
                        listener.onDateSet(null, year, monthPicker.getValue(), 1);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPicker.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
