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

import org.w3c.dom.Text;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class EntryFormActivity extends AppCompatActivity {
    private String[] arrObjectives;
    //private Bundle bundle;
    //private ArrayList<Record> recordList;
    RecordDbHelper dbHelper;
    Bundle bundle;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_form);

        // immediately make the delete button invisible, because we might not need it
        // it will be made visible when the edit variant of this activity is called
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.INVISIBLE);


        AutoCompleteTextView actv;
        actv = (AutoCompleteTextView) findViewById(R.id.actvObjective);

        arrObjectives = getResources().getStringArray(R.array.objectives);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrObjectives);
        actv.setAdapter(adapter);

        actv.setOnEditorActionListener(new DoneOnEditorActionListener());

        dbHelper = new RecordDbHelper(this);

        // all of this should only fire if EntryFormActivity was passed an int, and that int would be an ID from the db
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            editEntry(bundle);
        }

    }

    private void editEntry(Bundle bundle) {
        int recordID = bundle.getInt(MainActivity.KEY_EXTRA_RECORD_ID);
        Cursor rs = dbHelper.getRecord(recordID);
        rs.moveToFirst();
        String date = rs.getString(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_DATE));
        String opponent = rs.getString(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_OPPONENT));
        String points = rs.getString(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_POINTS));
        int order = rs.getInt(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_ORDER));
        int wipe = rs.getInt(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_ENDCON));
        String objective = rs.getString(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_OBJECTIVE));
        String playerFaction = rs.getString(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_FACTION));;
        String opFor = rs.getString(rs.getColumnIndex(RecordDbHelper.COLUMN_NAME_OPFOR));;
        if (!rs.isClosed()) {
            rs.close();
        }

        // I'm not sure I have to do this manually, but SQLite stores booleans as 0 or 1
        if(order == 1)
        {
            ((RadioButton) findViewById(R.id.radFirst)).setChecked(true);
        }
        else
        {
            ((RadioButton) findViewById(R.id.radSecond)).setChecked(true);
        }

        // default behaviour of this box is 'round limit' (0/unchecked/false)
        if(wipe == 1)
        {
            ((CheckBox) findViewById(R.id.cbxWipe)).setChecked(true);
        }

        // default behaviour of these buttons is unchecked (Imperials)
        if(playerFaction.equals("Rebels"))
        {
            ((ToggleButton) findViewById(R.id.tgbPlayer)).setChecked(true);
        }
        if(opFor.equals("Rebels"))
        {
            ((ToggleButton) findViewById(R.id.tgbPlayer)).setChecked(false);
        }

        // we require the last digit of the string of points, because the sequencing we set the spinner
        // this allows us to say that the last digit = the required index to pre-select
        int tourneyPoints = Character.getNumericValue(points.charAt(points.length() - 1));
        tourneyPoints = 10 - tourneyPoints;
        // this is so sloppy but it works so w/e
        if (tourneyPoints == 10) { tourneyPoints = 0; }

        // populate the remaining entry fields with the record contents
        ((TextView) findViewById(R.id.txtDate)).setText(date);
        ((EditText) findViewById(R.id.txtOpponent)).setText(opponent, TextView.BufferType.EDITABLE);
        ((Spinner) findViewById(R.id.spnPoints)).setSelection(tourneyPoints);
        ((AutoCompleteTextView) findViewById(R.id.actvObjective)).setText(objective, TextView.BufferType.EDITABLE);

        // change the text of the submit button to update to better reflect this variant of the window
        ((Button) findViewById(R.id.btnSubmit)).setText(R.string.btn_update);
        // also make the delete button visible, because that's something we could be doing in this activity
        btnDelete.setVisibility(View.VISIBLE);
    }

    public void deleteEntry(View v) {
        int recordID = bundle.getInt(MainActivity.KEY_EXTRA_RECORD_ID);
        dbHelper.deleteRecord(recordID);
        finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void setDate(String date) {
        TextView view = (TextView) findViewById(R.id.txtDate);
        view.setText(date);
    }

    public void formSubmit(View v) throws IncompleteFormException {
        // getting all of these values is as easy as taking the information from their fields
        String date = (String) ((TextView) findViewById(R.id.txtDate)).getText();
        String opponent = ((EditText) findViewById(R.id.txtOpponent)).getText().toString();
        String points = ((Spinner) findViewById(R.id.spnPoints)).getSelectedItem().toString();
        boolean turnOrder;
        boolean fleetWipe = ((CheckBox) findViewById(R.id.cbxWipe)).isChecked();
        String objective = ((AutoCompleteTextView) findViewById(R.id.actvObjective)).getText().toString();
        String playerFaction, opFor;

        boolean playerFac = ((ToggleButton) findViewById(R.id.tgbPlayer)).isChecked();
        boolean oppFac = ((ToggleButton) findViewById(R.id.tgbOpponent)).isChecked();

        if(playerFac)
        {
            playerFaction = "Rebels";
        }
        else
        {
            playerFaction = "Imperials";
        }

        if(oppFac)
        {
            opFor = "Rebels";
        }
        else
        {
            opFor = "Imperials";
        }

        try {


            // radio button is a bit trickier however
            // find the selected button in our radio group...
            int selectedRadio = ((RadioGroup) findViewById(R.id.rgpOrder)).getCheckedRadioButtonId();
            // if it's the first button...
            if (selectedRadio == R.id.radFirst) {
                turnOrder = true;
            }
            // if it's the second button...
            else if (selectedRadio == R.id.radSecond) {
                turnOrder = false;
            }
            // if no button was selected...
            else {
                throw new IncompleteFormException("Specify first or second");
            }

            // what a roundabout way of getting this
            // this throws a NumberFormatException if the first character isn't a number
            parseInt(Character.toString(date.charAt(0)));

            if (opponent.length() < 1) {
                // error, an opponent has to have a name longer than 0 characters
                throw new IncompleteFormException("Enter opponent name");
            }
            if (!Arrays.asList(arrObjectives).contains(objective)) {
                // error, the objective has to be an objective from the list
                throw new IncompleteFormException("Select objective from list");
            }

            /*
            Need to have an alternate option that updates instead of adding a new entry based on whether bundle is null or not
             */
            if(bundle != null) {
                dbHelper.updateRecord(bundle.getInt(MainActivity.KEY_EXTRA_RECORD_ID), date, opponent, playerFaction, opFor, points, turnOrder, objective, fleetWipe);
            }
            else {
                dbHelper.insertRecord(date, opponent, playerFaction, opFor, points, turnOrder, objective, fleetWipe);
            }
            finish();
        }
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
