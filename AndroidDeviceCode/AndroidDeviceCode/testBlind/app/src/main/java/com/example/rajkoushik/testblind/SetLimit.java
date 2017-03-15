package com.example.rajkoushik.testblind;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;


//Activity for setting the change difference to to publish updates from the PI
public class SetLimit extends AppCompatActivity {

    private final static String serverURL = ConnectionActivity.globalIpValue;;
    static String changevalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);
        setTitle("Change Notify");

        class SendTemperatureChange extends AsyncTask<Void, Void, String[]> {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String[] doInBackground(Void... params) {
                String[] arrString = {"", "", ""};
                return arrString;
            }

            protected void onProgressUpdate(Integer... progress) {

            }

            protected void onPostExecute(String result[]) {
                ArrayList<String> changeAsList = new ArrayList<>();
                changeAsList.add(changevalue);
                JSONHandler.ProcessListRequests(serverURL, changeAsList, "setNotifyLimit");
            }
        }

        Button button = (Button) findViewById(R.id.button2);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    EditText getChange = (EditText) findViewById(R.id.editText);
                    changevalue = getChange.getText().toString();
                    new SendTemperatureChange().execute();


                    //Sending Notification Update for Change Made
                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    Intent intent = new Intent(SetLimit.this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(SetLimit.this, (int) System.currentTimeMillis(), intent, 0);
                    Notification n  = new Notification.Builder(SetLimit.this)
                            .setContentTitle("Change in temperature sensitivity successful!")
                            .setContentText("Update to   :" + changevalue)
                                    .setSmallIcon(R.drawable.minus1)
                                    .setContentIntent(pIntent)
                                    .setAutoCancel(true).build();
                    notificationManager.notify(0, n);
                    Intent myIntent = new Intent(SetLimit.this, MainActivity.class);
                  SetLimit.this.startActivity(myIntent);

                }
            });
        }

    }
    }

