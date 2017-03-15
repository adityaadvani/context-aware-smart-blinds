package com.example.rajkoushik.testblind;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

//For Adding Rules Based on User Input Selected From the Selection Spinners
public class RulesActivity extends AppCompatActivity {
    String temperatureSelected;
    String conditionSelected;
    String lightSelected;
    String blindsSelected;
    private final static String serverURL = ConnectionActivity.globalIpValue;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Options of Various Selection of Rules Possible.
        String[] arraySpinner = new String[]{
                "freezing", "cold", "comfort", "warm", "hot"
        };

        String[] arraySpinner2 = new String[]{
                "and", "or"
        };


        String[] arraySpinner4 = new String[]{
                "dark", "dim", "bright"
        };

        String[] arraySpinner3 = new String[]{
                "open", "half", "close"
        };


        //Finding the Appropriate Spinner
        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        Spinner s1 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner2);
        s1.setAdapter(adapter1);

        Spinner s2 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner3);
        s2.setAdapter(adapter2);

        Spinner s3 = (Spinner) findViewById(R.id.spinner4);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner4);
        s3.setAdapter(adapter3);

        //Async Task for Fetching and Adding the Rules
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
            }

            protected void onPostExecute(String result[]) {
            }
        }

        //Get the Rule Selection from the spinners
        Button button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                    temperatureSelected = mySpinner.getSelectedItem().toString();

                    Spinner mySpinner2 = (Spinner) findViewById(R.id.spinner2);
                    conditionSelected = mySpinner2.getSelectedItem().toString();

                    Spinner mySpinner4 = (Spinner) findViewById(R.id.spinner4);
                    lightSelected = mySpinner4.getSelectedItem().toString();

                    Spinner mySpinner3 = (Spinner) findViewById(R.id.spinner3);
                    blindsSelected = mySpinner3.getSelectedItem().toString();

                    String toBeAdded = "IF " + temperatureSelected + " " + conditionSelected + " " + lightSelected + " " + "THEN " + blindsSelected;

                    if (RulesDisplayActivity.rules.contains(toBeAdded)) {
                        //Do Nothing
                    } else {
                        RulesDisplayActivity.rules.add(toBeAdded);
                        new SendJSONAddRequest().execute();
                    }
                    Intent myIntentRecreate = new Intent(RulesActivity.this, RulesDisplayActivity.class);
                    RulesActivity.this.startActivity(myIntentRecreate);
                    finish();

                }
            });

        }

    }


}
