package com.jshaak.armadacombatlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class RecordDBHelper extends SQLiteOpenHelper {

    // Schema information
    private static final String DATABASE_NAME = "Records.db";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_NAME = "records";
    static final String COLUMN_NAME_ID = "_id";
    static final String COLUMN_NAME_DATE = "date";
    static final String COLUMN_NAME_OPPONENT = "opponent";
    static final String COLUMN_NAME_FACTION = "playerfaction";
    static final String COLUMN_NAME_OPFOR = "opfor";
    static final String COLUMN_NAME_POINTS = "tourneypoints";
    static final String COLUMN_NAME_OBJECTIVE = "objective";
    static final String COLUMN_NAME_ORDER = "wentfirst";
    static final String COLUMN_NAME_ENDCON = "endcon";

    RecordDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    /*
    The core function of making the db if it doesn't exist shouldn't require much explanation, but this is
    an appropriate time for explaining the schema a bit.

    ID is automatically generated
    DATE is stored as a text because of SQLite
    OPPONENT is stored as a text since that's what human names are
    FACTION and OPFORE despite being a binary controller on the input form are stored as text. Possible schema update point
    POINTS is a pair of integers delimited by a '-', so it is stored as a text. Possible schema update point, only store player score instead
    OBJECTIVE is the name of an objective, and all objectives in Armada are strings therefore it's a text
    ORDER is a boolean, you went first and that's true or false. Numeric because that's how SQLite does booleans.
    ENDCON is a boolean just like ORDER. Fleet destroyed is either true or false.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_DATE + " TEXT," +
                COLUMN_NAME_OPPONENT + " TEXT," +
                COLUMN_NAME_FACTION + " TEXT," +
                COLUMN_NAME_OPFOR + " TEXT," +
                COLUMN_NAME_POINTS + " TEXT," +
                COLUMN_NAME_OBJECTIVE + " TEXT," +
                COLUMN_NAME_ORDER + " NUMERIC," +
                COLUMN_NAME_ENDCON + " NUMERIC)"
        );
    }

    /*
    Code executes when there's an old DATABASE_VERSION present.
    Right now it drops the table and that's not exactly what I want going forward.
    When I finish implementing it then I'm going to make it export to a spreadsheet or a csv or something first.
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
    this is a generic control and it isn't used right now, but it's future-proofing
    as such it's not currently getting refactored
    */
    @SuppressWarnings("unused")
    public void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    /*
    This is the meat of the application really. It will accept a parameter for every column in the database
    and then it will create that entry in the database.
    */
    void insertRecord(String strDate, String strOpponent, String strPlayerFaction, String strOpposingFaction, String strPoints,
                                boolean boolFirst, String strObjective, boolean boolWipe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_DATE, strDate);
        contentValues.put(COLUMN_NAME_OPPONENT, strOpponent);
        contentValues.put(COLUMN_NAME_FACTION, strPlayerFaction);
        contentValues.put(COLUMN_NAME_OPFOR, strOpposingFaction);
        contentValues.put(COLUMN_NAME_POINTS, strPoints);
        contentValues.put(COLUMN_NAME_OBJECTIVE, strObjective);
        contentValues.put(COLUMN_NAME_ORDER, boolFirst);
        contentValues.put(COLUMN_NAME_ENDCON, boolWipe);
        db.insert(TABLE_NAME, null, contentValues);
    }

    /*
    Contrasting insertRecord, this is the potatoes of the application. It will also accept a parameter for every column in the database
    however it will instead run an update on the entry that was being edited on EntryFormActivity.

    In theory I could only change the columns that were updated but it takes more code to check to see what was and wasn't changed than it
    does to just set them all to the same value again.
     */
    void updateRecord(Integer intPrimaryKey, String strDate, String strOpponent, String strPlayerFaction, String strOpposingFaction,
                                String strPoints, boolean boolFirst, String strObjective, boolean boolWipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_DATE, strDate);
        contentValues.put(COLUMN_NAME_OPPONENT, strOpponent);
        contentValues.put(COLUMN_NAME_FACTION, strPlayerFaction);
        contentValues.put(COLUMN_NAME_OPFOR, strOpposingFaction);
        contentValues.put(COLUMN_NAME_POINTS, strPoints);
        contentValues.put(COLUMN_NAME_OBJECTIVE, strObjective);
        contentValues.put(COLUMN_NAME_ORDER, boolFirst);
        contentValues.put(COLUMN_NAME_ENDCON, boolWipe);
        db.update(TABLE_NAME, contentValues, COLUMN_NAME_ID + " = ? ", new String[] { Integer.toString(intPrimaryKey) } );
    }

    /*
    This method is used to get the exact results of a single entry by finding it using the primary key. This is for EntryFormActivity to pull
    all of the information from a single entry so that you can see all of the information it links to.
     */
    Cursor getRecord(int intPrimaryKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NAME_ID + "=?", new String[] { Integer.toString(intPrimaryKey) } );
    }

    /*
    This is just a SELECT * FROM TABLE where it orders by date. This is an expansion option to change what you select, and maybe even the sort
    by date could be too. However, date right now is the most important way to sort because the app is supposed to be a history log of games.
    This method is used to populate the ListView on MainActivity.
     */
    Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM " + TABLE_NAME + " ORDER BY date(" + COLUMN_NAME_DATE + ") ASC", null );
    }

    /*
    Deletes a record. We ignore the int output because I currently only support deleting via primary key, which means that only one record
    should be deleted at a time anyway. SQLite is handling the assigning of primary keys so I can trust it to not have duplicates.
     */
    void deleteRecord(Integer intPrimaryKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ? ", new String[] { Integer.toString(intPrimaryKey) });
    }
}