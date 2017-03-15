package com.example.rajkoushik.testblind;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

//Activity for Displying the Rules, Has Add and Delete Options.
public class RulesDisplayActivity extends AppCompatActivity {

    static ArrayList<String> rules = new ArrayList<String>();
    private final static String serverURL = ConnectionActivity.globalIpValue;
    static ArrayList<ArrayList<String>> rulesAsList = new ArrayList<ArrayList<String>>();
    static ArrayList removeRule;
    static int gPosition;

    ArrayAdapter<String> adapter;
    String temperatureSelected;
    String conditionSelected;
    String lightSelected;
    String blindsSelected;


    //Rule to be sent to be added to Fuzzy Logic Rules
    class SendJSONAddRequest extends AsyncTask<Void, Void, String[]> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String[] doInBackground(Void... params) {
            ArrayList<String> addParameters = new ArrayList<>();
            String[] arrString = {"", "", ""};
            addParameters.add(temperatureSelected);
            addParameters.add(conditionSelected);
            addParameters.add(lightSelected);
            addParameters.add(blindsSelected);
            RulesDisplayActivity.rulesAsList.add(addParameters);
            String gotResposne = JSONHandler.ProcessListRequests(serverURL, addParameters, "addRule");
            return arrString;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result[]) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_display);
        setTitle("My Rules");
        final ListView rulesList = (ListView) findViewById(R.id.listViewRules);
        adapter = new ArrayAdapter<String>(this, R.layout.listview, rules);

        if (rulesList != null) {
            rulesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        rulesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gPosition = position;
            }
        });
        // specify the list adaptor
        rulesList.setAdapter(adapter);

        class SendJSONRemoveRule extends AsyncTask<Void, Void, String[]> {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String[] doInBackground(Void... params) {
                String[] arrString = {"", "", ""};
                String gotResposne = JSONHandler.ProcessListRequests(serverURL, removeRule, "removeRule");
                return arrString;
            }

            protected void onProgressUpdate(Integer... progress) {
            }

            protected void onPostExecute(String result[]) {
            }
        }

        //Starting Add Rules Activity UI
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(RulesDisplayActivity.this, RulesActivity.class);
                    RulesDisplayActivity.this.startActivity(myIntent);
                    finish();

                }
            });
        }

        //Deleting Rules
        FloatingActionButton fabminus = (FloatingActionButton) findViewById(R.id.fabminus);
        if (fabminus != null) {
            fabminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = view.toString();
                    rules.remove(gPosition);
                    removeRule = rulesAsList.get(gPosition);
                    rulesAsList.remove(gPosition);
                    new SendJSONRemoveRule().execute();
                    rulesList.setAdapter(adapter);
                }
            });
        }

    }

}
