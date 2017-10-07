package com.jshaak.armadacombatlog;

import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

/*
    This class shows all the verbose details of any given record, and is also how we can add, remove or update any given record.

    Currently there are no major plans to update, but I may add support for CC campaign games. That will likely require a schema update to the DB,
    so it's going to absolutely wait until after export functionality is done. I have ideas for how to handle it though.
 */

public class EntryFormActivity extends AppCompatActivity {
    // the array that holds all of the objectives, loaded from the xml
    private String[] arrObjectives;

    // while technically a hit to system resources, the app should be lightweight enough that giving both acitivities their own RecordDBHelper to use should be fine
    private RecordDBHelper dbHelper;

    // filled in onCreate, this bundle currently only intends to hold the primary key for an entry if one was passed
    private Bundle bundle;

    // in onCreate this is set to invisible, and it will be set visible if we're editing an entry
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_form);

        // immediately make the delete button invisible, because we might not need it
        // it will be made visible when the edit variant of this activity is called
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.INVISIBLE);


        AutoCompleteTextView actvObjective = (AutoCompleteTextView) findViewById(R.id.actvObjective);

        // note that currently this list does not contain CC campaign exclusive objectives
        arrObjectives = getResources().getStringArray(R.array.objectives);
        // immediately bind arrObjectives as a simple list to the autocomplete textbox
        actvObjective.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrObjectives));

        // this is functionality to change the enter or next to done when using this textbox
        actvObjective.setOnEditorActionListener(new DoneOnEditorActionListener());

        // the dbHelper that belongs to EntryFormActivity
        dbHelper = new RecordDBHelper(this);

        // all of this should only fire if EntryFormActivity was passed an int, and that int would be a primary key
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            displayRecord(bundle);
        }

    }

    // if we had a bundle, it has a primary key in it. Time to use that primary key to query the DB for that entry and populate the entry form with that info.
    private void displayRecord(Bundle bundle) {
        Cursor crsRecord = dbHelper.getRecord(bundle.getInt(MainActivity.KEY_EXTRA_RECORD_ID));
        // start interacting with the first item in the result (which should also be the only one)
        crsRecord.moveToFirst();

        // these first four are easy, since they're stored as strings anyway
        String strDate = crsRecord.getString(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_DATE));
        String strOpponent = crsRecord.getString(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_OPPONENT));
        String strPoints = crsRecord.getString(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_POINTS));
        String strObjective = crsRecord.getString(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_OBJECTIVE));

        // these are SQLite booleans. Which means they're either 0 for false or 1 for true.
        int intOrder = crsRecord.getInt(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_ORDER));
        int intWipe = crsRecord.getInt(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_ENDCON));

        // these are stored as strings, but they require a little bit more manipulation first
        String playerFaction = crsRecord.getString(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_FACTION));
        String opFor = crsRecord.getString(crsRecord.getColumnIndex(RecordDBHelper.COLUMN_NAME_OPFOR));

        // close our cursor when we're finished with it
        if (!crsRecord.isClosed()) {
            crsRecord.close();
        }

        // if boolean turnOrder is true, that means firstPlayer radio needs to be selected.
        if(intOrder == 1)
        {
            ((RadioButton) findViewById(R.id.radFirst)).setChecked(true);
        }
        else
        {
            ((RadioButton) findViewById(R.id.radSecond)).setChecked(true);
        }

        // default behaviour of this box is 'round limit' (0/unchecked/false)
        if(intWipe == 1)
        {
            ((CheckBox) findViewById(R.id.cbxWipe)).setChecked(true);
        }

        /*
        default behaviour of the opponent button is to remain unchecked and default to Imperial. This is a stylistic choice, since
        outside of a CC campaign there's no guarantee which faction your opponent will be

        There is more of a guarantee about which faction the app user will be, since building competitive fleets for both sides is
        significantly more expensive. To future-proof, I'm not going to abuse the current default of tgbPlayer to be checked (Rebels).
        */
        ToggleButton tgbPlayerFaction = (ToggleButton) findViewById(R.id.tgbPlayer);
        if(playerFaction.equals("Imperials"))
        {
            tgbPlayerFaction.setChecked(false);
        }
        else
        {
            tgbPlayerFaction.setChecked(true);
        }
        if(opFor.equals("Rebels"))
        {
            ((ToggleButton) findViewById(R.id.tgbOpponent)).setChecked(true);
        }

        /*
        we require the last digit of the string of points, because the sequencing we set the spinner
        this allows us to say that 10 - the last digit is equal to the index we want to pick.
        This doesn't quite work in the 10 case because we get the last character and don't treat it as
        a dash delimited string. The latter will take more steps of code than this, for marginal improvement of readability.
        So in that case we just say if it's 10 it was supposed to be index 0, where 1-10 is stored.
        */
        int intPoints = Character.getNumericValue(strPoints.charAt(strPoints.length() - 1));
        intPoints = 10 - intPoints;
        if (intPoints == 10) { intPoints = 0; }

        // populate the remaining entry fields with the record contents
        ((TextView) findViewById(R.id.txtDate)).setText(strDate);
        ((EditText) findViewById(R.id.txtOpponent)).setText(strOpponent, TextView.BufferType.EDITABLE);
        ((Spinner) findViewById(R.id.spnPoints)).setSelection(intPoints);
        ((AutoCompleteTextView) findViewById(R.id.actvObjective)).setText(strObjective, TextView.BufferType.EDITABLE);

        // change the text of the submit button to update to better reflect this variant of the window
        ((Button) findViewById(R.id.btnSubmit)).setText(R.string.btn_update);
        // also make the delete button visible, because that's something we could be doing in this activity
        btnDelete.setVisibility(View.VISIBLE);
    }

    // Because this is a button press, I believe I need to pass the View in Android Studio even though I don't use it
    public void deleteEntry(@SuppressWarnings("UnusedParameters") View v) {
        // delete this record by the primary key we used to find it
        dbHelper.deleteRecord(bundle.getInt(MainActivity.KEY_EXTRA_RECORD_ID));
        finish();
    }

    // make the pop-up where the user selects the date off of a calendar, better than typing for most dates
    public void showDatePickerDialog(@SuppressWarnings("UnusedParameters") View v) {
        DialogFragment dfDialogFrag = new DatePickerFragment();
        dfDialogFrag.show(getFragmentManager(), "datePicker");
    }

    // Generic set method for date, used by DatePickerFragment to interact from that class
    public void setDate(String date) {
        ((TextView) findViewById(R.id.txtDate)).setText(date);
    }

    // Generic get method for date, also used by DatePickerFragment to open the picker to the selected date if there is one
    public String getDate() {
        return (String) ((TextView) findViewById(R.id.txtDate)).getText();
    }

    /*
    One of two heavy lifters in EntryFormActivity. This is bound to a button, so again I believe Android Studio requires it to accept a View
    even though I really don't care about that.

    This method is going to parse all of the input fields for data, and then it's going to use dbHelper to make an insert on the database
    That means this form also has to handle all validation.
    The following entries do not have default values and therefore must be supplied by the user:
        Date, Opponent Name, Turn Order, Objective
    The following entries do have default values:
        Tournament Points, Fleet Wipe Status, Player Faction, Opponent Faction
     */
    public void formSubmit(@SuppressWarnings("UnusedParameters") View v) {
        // getting all of these values is basically as easy as taking the string from their fields
        String strDate = (String) ((TextView) findViewById(R.id.txtDate)).getText();
        String strOpponent = ((EditText) findViewById(R.id.txtOpponent)).getText().toString();
        String strPoints = ((Spinner) findViewById(R.id.spnPoints)).getSelectedItem().toString();
        String strObjective = ((AutoCompleteTextView) findViewById(R.id.actvObjective)).getText().toString();

        // turn order is set via radio buttons, but by default neither is selected, so that's going to be handled further below. True if first.
        boolean boolOrder;
        // The loser was completely eliminted or not (you auto-lose if you're wiped). Default value is that the game went to rounds, which is more common in Armada.
        boolean boolWipe = ((CheckBox) findViewById(R.id.cbxWipe)).isChecked();

        /*
        It would require a schema update to change the storage of player and opponent factions to booleans.
        While I seriously doubt that Armada will ever get additional factions, it could in theory happen so I guess this is future-proofing?
        */
        String strPlayerFac, strOpFor;

        // if these togglebuttons are checked, they're Rebels. If unchecked, Imperials.
        if(((ToggleButton) findViewById(R.id.tgbPlayer)).isChecked())
        {
            strPlayerFac = "Rebels";
        }
        else
        {
            strPlayerFac = "Imperials";
        }
        if(((ToggleButton) findViewById(R.id.tgbOpponent)).isChecked())
        {
            strOpFor = "Rebels";
        }
        else
        {
            strOpFor = "Imperials";
        }

        /* Basically all of the options that require user input will be in a try/catch to make informing them of errors easier
        * one error at a time might be suboptimal, but I wanted to use toasts because the form is short enough that you can see the whole thing
        * without any scrolling.
        * */
        try {
            // First turn order via radio buttons; start by getting the selected button
            int intSelectedRadio = ((RadioGroup) findViewById(R.id.rgpOrder)).getCheckedRadioButtonId();

            if (intSelectedRadio == R.id.radFirst) {
                boolOrder = true;
            }
            else if (intSelectedRadio == R.id.radSecond) {
                boolOrder = false;
            }
            // if neither first or second was chosen, that's an error and we're done here. Don't waste time with the rest of the form.
            else {
                throw new IncompleteFormException("Specify first or second");
            }

            // If the first character of the YYYY-MM-DD string isn't an int chances are it's not a date; this throws a NumberFormatException (slightly easier to code)
            //noinspection ResultOfMethodCallIgnored
            parseInt(Character.toString(strDate.charAt(0)));

            // Next check that there's at least one character in the name
            if (strOpponent.length() < 1) {
                throw new IncompleteFormException("Enter opponent name");
            }

            // Finally check that the objective is actually one of the ones from the list
            if (!Arrays.asList(arrObjectives).contains(strObjective)) {
                throw new IncompleteFormException("Select objective from list");
            }

            // The bundle wasn't null if this is an update, so update that record using the contents of our bundle (which is a primary key for that entry)
            if(bundle != null) {
                dbHelper.updateRecord(bundle.getInt(MainActivity.KEY_EXTRA_RECORD_ID), strDate, strOpponent, strPlayerFac, strOpFor, strPoints, boolOrder, strObjective, boolWipe);
            }
            // otherwise we just stick it into the database as a new entry
            else {
                dbHelper.insertRecord(strDate, strOpponent, strPlayerFac, strOpFor, strPoints, boolOrder, strObjective, boolWipe);
            }
            finish();
        }
        // the catches below will just fire some toasts with short error messages
        catch (IncompleteFormException e){
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (NumberFormatException e) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Select a date", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
