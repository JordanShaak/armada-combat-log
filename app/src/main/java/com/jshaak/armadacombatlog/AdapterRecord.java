package com.jshaak.armadacombatlog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan Shaak on 9/15/2017.
 */

public class AdapterRecord extends ArrayAdapter<Record> {
    private Activity activity;
    private ArrayList<Record> arlRecord;
    private static LayoutInflater inflater = null;

    public AdapterRecord(@NonNull Context context, @LayoutRes int resource, @NonNull List<Record> objects) {
        super(context, resource, objects);
    }

}