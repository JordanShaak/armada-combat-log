package com.jshaak.armadacombatlog;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class NotSoSimpleCursorAdapter extends SimpleCursorAdapter {

    public NotSoSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_main, parent, false);
    }
    // The bindView method is used to bind all data to a given view
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView previewField = view.findViewById(R.id.text_entry);
        // Extract properties from cursor
        // getString(int) - number of column
        // 0 = id, 1 = date, 2 = name, 3 = faction, 4 = opfor, 5 = score
        String date = cursor.getString(1);
        String name = cursor.getString(2);
        String score = cursor.getString(5);
        int lastDigit = Character.getNumericValue(score.charAt(score.length() - 1));

        if(lastDigit > 5 || lastDigit == 0) {
            previewField.setTextColor(Color.RED);
        }
        else {
            previewField.setTextColor(Color.GREEN);
        }
        previewField.setText(date + " - " + name + " (" + score + ")");
        //txt2.setText("Opp: " + name);
        //txt3.setText("Score: " + score);

    }
}