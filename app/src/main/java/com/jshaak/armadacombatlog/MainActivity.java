package com.jshaak.armadacombatlog;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private ArrayList<Record> recordList = new ArrayList<Record>();
    public ListView listView;
    //public AdapterRecord adbRecord;
    public final static String KEY_EXTRA_RECORD_ID = "KEY_EXTRA_RECORD_ID";

    RecordDbHelper dbHelper;
    NotSoSimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        dbHelper = new RecordDbHelper(this);

        final Cursor cursor = dbHelper.getAllRecords();

        String [] columns = new String[] {
                RecordDbHelper.COLUMN_NAME_DATE,
                RecordDbHelper.COLUMN_NAME_OPPONENT,
                RecordDbHelper.COLUMN_NAME_POINTS
        };

        // I don't know what the fuck this does, but I used to need it
        int [] widgets = new int[] {
                0
        };

        //SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor, columns, widgets, 0);
        cursorAdapter = new NotSoSimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, columns, widgets, 0);

        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView adpView, View view,
                                                                    int position, long id) {
                                                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);
                                                int recordID = itemCursor.getInt(itemCursor.getColumnIndex(RecordDbHelper.COLUMN_NAME_ID));
                                                Intent intent = new Intent(getApplicationContext(), EntryFormActivity.class);
                                                intent.putExtra(KEY_EXTRA_RECORD_ID, recordID);
                                                startActivity(intent);
                                            }
                                        });

        /*
        Willing to bet that when I want to load the saved logs from a file, it has to be done here
         */
        // let's make a few records and add them to the ArrayList to test
        //recordList.add(new Record("2017-9-11","Brian","Rebels","Imperials","1-10",true,"Contested Outpost",true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Cursor cursorUpdate = dbHelper.getAllRecords();
        cursorAdapter.changeCursor(cursorUpdate);
    }

    // Called when the user hits Add Result button
    public void addResult(View view) {
        // stuff
        Intent intent = new Intent(this, EntryFormActivity.class);
        startActivity(intent);
    }
}
