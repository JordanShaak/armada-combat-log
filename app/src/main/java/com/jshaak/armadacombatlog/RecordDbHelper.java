package com.jshaak.armadacombatlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Records.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME = "records";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_OPPONENT = "opponent";
    public static final String COLUMN_NAME_FACTION = "playerfaction";
    public static final String COLUMN_NAME_OPFOR = "opfor";
    public static final String COLUMN_NAME_POINTS = "tourneypoints";
    public static final String COLUMN_NAME_OBJECTIVE = "objective";
    public static final String COLUMN_NAME_ORDER = "wentfirst";
    public static final String COLUMN_NAME_ENDCON = "endcon";

    public RecordDbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertRecord(String date, String opponent, String playerFaction, String opFor, String tourneyPoints,
                                boolean wentFirst, String objective, boolean fleetWipe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_DATE, date);
        contentValues.put(COLUMN_NAME_OPPONENT, opponent);
        contentValues.put(COLUMN_NAME_FACTION, playerFaction);
        contentValues.put(COLUMN_NAME_OPFOR, date);
        contentValues.put(COLUMN_NAME_POINTS, tourneyPoints);
        contentValues.put(COLUMN_NAME_OBJECTIVE, objective);
        contentValues.put(COLUMN_NAME_ORDER, wentFirst);
        contentValues.put(COLUMN_NAME_ENDCON, fleetWipe);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateRecord(Integer id, String date, String opponent, String playerFaction, String opFor, String tourneyPoints,
                                boolean wentFirst, String objective, boolean fleetWipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_DATE, date);
        contentValues.put(COLUMN_NAME_OPPONENT, opponent);
        contentValues.put(COLUMN_NAME_FACTION, playerFaction);
        contentValues.put(COLUMN_NAME_OPFOR, date);
        contentValues.put(COLUMN_NAME_POINTS, tourneyPoints);
        contentValues.put(COLUMN_NAME_OBJECTIVE, objective);
        contentValues.put(COLUMN_NAME_ORDER, wentFirst);
        contentValues.put(COLUMN_NAME_ENDCON, fleetWipe);
        db.update(TABLE_NAME, contentValues, COLUMN_NAME_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NAME_ID + "=?", new String[] { Integer.toString(id) } );
    }
    public Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM " + TABLE_NAME + " ORDER BY date(" + COLUMN_NAME_DATE + ") ASC", null );
    }

    public Integer deleteRecord(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_NAME_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }


}
