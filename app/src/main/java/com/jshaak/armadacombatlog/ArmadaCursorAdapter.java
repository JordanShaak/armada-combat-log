package com.jshaak.armadacombatlog;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Custom SimpleCursorAdapter mostly used to modify the visual appearance of items on the MainActivity ListView
 */


class ArmadaCursorAdapter extends SimpleCursorAdapter {

    ArmadaCursorAdapter(Context context, Cursor c, String[] from) {
        /*
        I feel like there's a better way to do this, but this is actually very simple so I'm not sure if there's anything else to be done.

        Some of these variables are always fixed in my code. The first is flags, which is set to 0 here since I don't use it.
        The second is to, I don't use it either so I always pass an array with only one value: 0. However it has to be created in the method
        that calls this constructor. Third is the layout, which currently I just use simple_List_item_1.
        Fourth and fifth would be from and to, since I manually bind the results in bindView and therefore I don't use that functionality.
        */
        super(context, android.R.layout.simple_list_item_1, c, from, null, 0);
    }

    /*
    The newView method is used to inflate a new view and return it,
    you don't bind any data to the view at this point.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_main, parent, false);
    }

    // The bindView method is what actually takes the data and sticks it in each entry
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView txtListItem = view.findViewById(R.id.text_entry);
        /*
        Extract properties from cursor
        getString(int) - number of column
        0 = id, 1 = date, 2 = name, 3 = faction, 4 = opfor, 5 = score
        */
        String strScore = cursor.getString(5);

        // we use the final digit of the score to determine whether it was a win or loss
        int intLast = Character.getNumericValue(strScore.charAt(strScore.length() - 1));
        /*
        losses are 5-6, 4-7, 3-8, 2-9 and 1-10
        this could be reworked to parse the score as a delimited string to avoid the second part of the if condition
        */
        if(intLast > 5 || intLast == 0) {
            // losses are red
            txtListItem.setTextColor(Color.RED);
        }
        else {
            // wins are green
            txtListItem.setTextColor(Color.GREEN);
        }

        /*
        this could later be expanded to use additional text fields for further styling
        however for now I use a simple one text field entry because it's sufficient
        also see the comment earlier in this method for notes about the cursor mapping to the columns
        1 is the date, 2 is the opponent name
        */
        txtListItem.setText(cursor.getString(1) + " - " + cursor.getString(2) + " (" + strScore + ")");
    }
}