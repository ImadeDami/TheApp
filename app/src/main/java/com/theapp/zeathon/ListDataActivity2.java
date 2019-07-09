package com.theapp.zeathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListDataActivity2 extends AppCompatActivity {
    private static final String TAG = "ListDataActivity2";
    ListView listView;
    DBHelper mDataBaseHelper;
    BroadcastReceiver broadcastReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listview);
        mDataBaseHelper = new DBHelper(this);

        populateListView();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
             populateListView();
            }
        };

    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

// get all data and append to list
        Cursor data = mDataBaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()){
            listData.add(data.getString(1));
        }

        //create the list adapter and set adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
        ((ArrayAdapter) adapter).notifyDataSetChanged();

    }
}
