package com.jshaak.armadacombatlog;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/*
    This class is home to the ListView that will show all records in the database, and it also functions as a main menu.
    Currently the only navigation options deeper into the app are clicking a button to add a new record, or clicking on
    any existing record to view more details about that record (and edit if desired).

    Future expansion plans include adding an export function that will be accessible from this menu, and possibly a
    way to set preferences (though currently the only preference that could be changed is user default faction).
    Also an export function should probably be met with an import function. Can't think of why I'd have one and not the other.
 */

public class MainActivity extends AppCompatActivity {

    // this constant is just used to fish for the primary key out of the intent in EntryFormActivity
    public final static String KEY_EXTRA_RECORD_ID = "KEY_EXTRA_RECORD_ID";

    // this ListView is the main component on MainActivity; to my understanding it needs to be here because of the cursorAdapter
    private ListView listView;
    // we can use the same RecordDBHelper for everything we need, so just make one and keep it around
    private RecordDBHelper dbHelper;
    // required in onCreate for the initial binding of data, but also in onResume to update it
    private ArmadaCursorAdapter acaCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // map the ListView to our variable, we're going to start manipulating it right away
        listView = (ListView) findViewById(R.id.listView);

        // same goes for our dbHelper
        dbHelper = new RecordDBHelper(this);

        /*
        this right now is a bit of a work-around. This shouldn't be necessary but the app crashes without it.
        As far as I know I don't actually use this, but that means that my understanding of SQLiteOpenHelper could use some work
        */
        String[] from = {
               RecordDBHelper.COLUMN_NAME_OPPONENT
        };

        // Create the ArmadaCursorAdapter which is going to be used to bind all the data to the ListView
        acaCursorAdapter = new ArmadaCursorAdapter(this, dbHelper.getAllRecords(), from);

        // this connects the ArmadaCursorAdapter filled with our query results to the component the user interacts with
        listView.setAdapter(acaCursorAdapter);

        // this code is what allows us to make an EntryFormActivity and hand it the primary key of the entry we've clicked on to edit
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView adpView, View view,
                                                                    int position, long id) {
                                                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);
                                                int intPrimaryKey = itemCursor.getInt(itemCursor.getColumnIndex(RecordDBHelper.COLUMN_NAME_ID));
                                                Intent intent = new Intent(getApplicationContext(), EntryFormActivity.class);
                                                intent.putExtra(KEY_EXTRA_RECORD_ID, intPrimaryKey);
                                                startActivity(intent);
                                            }
                                        });
    }

    /*
    whenever we return to MainActivity I want to refresh the AramdaCursorAdapter again with a new getAllRecords query
    this ensures that no matter how we end up back on MainActivity, stale records will not be there and new records will be visible immediately
    */
    @Override
    protected void onResume() {
        super.onResume();
        acaCursorAdapter.changeCursor(dbHelper.getAllRecords());
    }

    // Called when the user hits Add Result button; primary purpose is to open up an EntryFormActivity
    public void addResult(@SuppressWarnings("UnusedParameters") View view) {
        startActivity(new Intent(this, EntryFormActivity.class));
    }
}
