package com.jshaak.armadacombatlog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import java.util.Calendar;

/**
 * Yanked from a blog somewhere
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        // Month++ right away because the calendar uses 0-11 instead of the 1-12 humans are used to
        month++;
        String strMonth = "" + month, strDay =  "" + day;

        if(month < 10) { strMonth = "0" + month; }
        if(day < 10) { strDay = "0" + day; }

        String dateString = year + "-" + strMonth + "-" + strDay;
        ((EntryFormActivity) getActivity()).setDate(dateString);
    }
}