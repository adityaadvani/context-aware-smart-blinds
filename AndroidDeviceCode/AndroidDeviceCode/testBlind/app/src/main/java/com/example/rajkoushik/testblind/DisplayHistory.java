package com.example.rajkoushik.testblind;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DisplayHistory extends AppCompatActivity {
    static ArrayList<String> displayHistory = new ArrayList<String>();
    static int gPosition;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Change History");
        setContentView(R.layout.activity_display_history);
        if (displayHistory.isEmpty()) {
            displayHistory.add("  Date&Time      " + (char) 0x00B0 + "T           Ambient      Blinds");

        }
        final ListView displayHistoryList = (ListView) findViewById(R.id.displayView);
        adapter = new ArrayAdapter<String>(this, R.layout.displayview, displayHistory);
        displayHistoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        displayHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gPosition = position;
            }
        });
        displayHistoryList.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = view.toString();
                    displayHistory.remove(gPosition);
                    displayHistoryList.setAdapter(adapter);
                }
            });
        }
    }
}
