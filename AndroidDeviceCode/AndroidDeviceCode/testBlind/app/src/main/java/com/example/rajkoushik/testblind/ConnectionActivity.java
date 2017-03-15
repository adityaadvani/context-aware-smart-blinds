package com.example.rajkoushik.testblind;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionActivity extends AppCompatActivity {

    static String globalIpValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        setTitle("Connect");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.button3);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    EditText getChange = (EditText) findViewById(R.id.editText2);
                    globalIpValue = getChange.getText().toString()+ ":8080";
                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(ConnectionActivity.this, (int) System.currentTimeMillis(), intent, 0);
                    Notification n  = new Notification.Builder(ConnectionActivity.this)
                            .setContentTitle("IP is Set!")
                            .setContentText("Blinds Connection Success!")
                            .setSmallIcon(R.drawable.minus1)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true).build();
                    notificationManager.notify(0, n);
                    Intent myIntent = new Intent(ConnectionActivity.this, MainActivity.class);
                    ConnectionActivity.this.startActivity(myIntent);
                }
            });
        }
    }
}
