package com.jshaak.armadacombatlog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import java.util.Calendar;

/**
 * Initial version from https://android--examples.blogspot.com/2015/05/how-to-use-datepickerdialog-in-android.html
 *
 * With modifications to my specifications of course. Code to use the previously entered date and also some various refactoring courtesy of myself.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Retrieve the date from EntryFormActivity and split it by dashes
        String[] arrComponents = ((EntryFormActivity) getActivity()).getDate().split("-");

        // This array should be exactly 3 long if the YYYY-MM-DD date was present
        if(arrComponents.length == 3)
        {
            try {
                // required again because the calendar uses 0-11 for months whereas humans use 1-12
                int intMonthCal = Integer.parseInt(arrComponents[1]) - 1;
                return new DatePickerDialog(getActivity(), this, Integer.parseInt(arrComponents[0]), intMonthCal, Integer.parseInt(arrComponents[2]));
            }
            catch (NumberFormatException exc) {
                // I'm not sure it's bad practice, but if trying to parse the date from the text box fails I just want to fall back to using the current date
            }
        }

        // Uses the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

    }

    public void onDateSet(DatePicker view, int intYear, int intMonth, int intDay) {
        // Do something with the date chosen by the user
        // Month++ right away because the calendar uses 0-11 instead of the 1-12 humans are used to
        intMonth++;

        // Both of these if statements will catch months or days that would be a single digit and force double digit notation
        String strMonth = (intMonth < 10) ? "0" + intMonth : "" + intMonth;
        String strDay = (intDay < 10 ) ? "0" + intDay : "" + intDay;

        // Set the field on the EntryForm to a YYYY-MM-DD format of the date selected
        ((EntryFormActivity) getActivity()).setDate(intYear + "-" + strMonth + "-" + strDay);
    }
}