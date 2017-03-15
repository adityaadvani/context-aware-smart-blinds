package com.example.rajkoushik.testblind;
/*
MainActivity:Launcher for Async Tasks, Services and UI threads
By Aditya & Raj
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.*;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static TextView temperature;
    public static TextView ambient;
    public static TextView blindState;
    static String serverURL = ConnectionActivity.globalIpValue;
    static String ipAddr;
    public static String prevBlindState = "";
    public static String prevAmbiState = "";
    static String t1 = "", t2 = "", t3 = "", t4 = "";
    static String ReceivedString = "";
    static String rule = "";
    static String[] rules;
    static ArrayList<ArrayList<String>> outerList = new ArrayList<>();
    static ArrayList<String> innerList;
    static int onOrOff = 1;

    //Heavy duty UI updates run as a java thread
    public class uithread implements Runnable {
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new UpdateUI().execute();
        }
    }

    //Implementing Broadcast Receiver to catch update Intents
    public class GetNotifications extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String notifications = intent.getStringExtra("tempUpdate");
        }
    }


    //On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SmartBlinds");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Starting Server to receive notifications from the PI
        //Part of Pup-Sub model for communication
        Intent notifyIntent = new Intent(MainActivity.this, JsonRpcServer.class);
        startService(notifyIntent);


        //Set Navigation Items
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get Ip address of user Android Device and Send it to PI
        ipAddr = getLocalIpAddress();
        new SendIpAddress().execute();

        //Stitch to detect the option for Temperature Scale Selected
        Switch cOrF = (Switch) findViewById(R.id.switch1);
        cOrF.setChecked(false);
        cOrF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //Switch Change Listner
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    //Implies Temperature is requested in F
                    onOrOff = 0;
                } else {
                    //Implies Temperature is requested in C
                    onOrOff = 1;
                }
            }
        });
        Thread t = new Thread(new uithread());
        t.start();
        new GetRules().execute();

    }

    //Sending Ip address as an async task Android Device Subscribing to PI Updates
    class SendIpAddress extends AsyncTask<Void, Void, String[]> {
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
            ArrayList<String> ipAsList = new ArrayList<>();
            ipAsList.add(ipAddr);
            JSONHandler.ProcessListRequests(serverURL, ipAsList, "setIP");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.updateLimit) {

            Intent myIntent = new Intent(MainActivity.this, SetLimit.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }
        if (id == R.id.changeIp) {
            Intent myIntent = new Intent(MainActivity.this, ConnectionActivity.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.displayRules) {
            Intent myIntent = new Intent(MainActivity.this, RulesDisplayActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.displayHistory) {
            Intent myIntent = new Intent(MainActivity.this, DisplayHistory.class);
            MainActivity.this.startActivity(myIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class SendJSONRequest extends AsyncTask<Void, Void, String[]> {
        String tempFetched = "", ambiFetched = "", blindsFetched = "";

        protected void onPreExecute() {
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String[] arrString = {"", "", ""};
            tempFetched = JSONHandler.tempval;
            ambiFetched = JSONHandler.ambval;
            blindsFetched = JSONHandler.blindval;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd-HH:mm");
            String currentDateandTime = sdf.format(new Date());
            return arrString;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result[]) {
            String temp1 = "";

            temperature = (TextView) findViewById(R.id.temperature);
            ambient = (TextView) findViewById(R.id.ambient);
            blindState = (TextView) findViewById(R.id.blindState);


            if (onOrOff == 1) {
                temperature.setText(tempFetched + (char) 0x00B0 + "C");
            }
            if (onOrOff == 0) {
                int temp = Integer.parseInt(tempFetched);
                tempFetched = String.valueOf((int) ((temp) * (((float) 9 / (float) 5)) + 32));
                temperature.setText(tempFetched + (char) 0x00B0 + "F");
            }
            ambient.setText(ambiFetched);
            blindState.setText(blindsFetched);

            do {
                temp1 = JSONHandler.tempval;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (temp1.equals(tempFetched));


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new UpdateUI().execute();

        }

    }

    class UpdateUI extends AsyncTask<Void, Void, String[]> {
        String tempFetched = "", ambiFetched = "", blindsFetched = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String[] arrString = {"", "", ""};
            tempFetched = JSONHandler.tempval;
            ambiFetched = JSONHandler.ambval;
            blindsFetched = JSONHandler.blindval;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd-HH:mm");
            String currentDateandTime = sdf.format(new Date());
            return arrString;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result[]) {
            String temp1 = "";
            String History = "";

            if (!(prevBlindState.equals(blindsFetched)) || !(prevAmbiState.equals(ambiFetched))) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd-HH:mm");
                String currentDateandTime = sdf.format(new Date());

                if (onOrOff == 1) {
                    History += currentDateandTime + "    " + tempFetched + " " + (char) 0x00B0 + "C  " + "    " + ambiFetched + "       " + blindsFetched;
                } else {
                    int temp = Integer.parseInt(tempFetched);
                    tempFetched = String.valueOf((int) ((temp) * (((float) 9 / (float) 5)) + 32));

                    History += currentDateandTime + "    " + tempFetched + " " + (char) 0x00B0 + "F  " + "    " + ambiFetched + "       " + blindsFetched;
                }

                if (DisplayHistory.displayHistory.isEmpty()) {
                    DisplayHistory.displayHistory.add("  Date&Time      " + (char) 0x00B0 + "T           Ambient      Blinds");
                }

                DisplayHistory.displayHistory.add(1, History);
                prevBlindState = blindsFetched;
                prevAmbiState = ambiFetched;

                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent, 0);


                Notification n = new Notification.Builder(MainActivity.this)
                        .setContentTitle("Blinds State Changed ")
                        .setContentText(blindsFetched)
                        .setSmallIcon(R.drawable.minus1
                        )
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                notificationManager.notify(0, n);
            }

            temperature = (TextView) findViewById(R.id.temperature);
            ambient = (TextView) findViewById(R.id.ambient);
            blindState = (TextView) findViewById(R.id.blindState);


            if (onOrOff == 1) {
                temperature.setText(tempFetched + (char) 0x00B0 + "C");
            }
            if (onOrOff == 0) {
                int temp = Integer.parseInt(tempFetched);
                tempFetched = String.valueOf((int) ((temp) * (((float) 9 / (float) 5)) + 32));
                temperature.setText(tempFetched + (char) 0x00B0 + "F");
            }

            ambient.setText(ambiFetched);
            blindState.setText(blindsFetched);

            Thread t1 = new Thread(new uithread());
            t1.start();
        }
    }


    //Get the Rules From the Fuzzy Rules
    //For Reconfiguring with users previous choice: Persistent Storage
    //Set Rules in th euser device

    class GetRules extends AsyncTask<Void, Void, String[]> {
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
            ArrayList<String> getRulesAsList = new ArrayList<String>();
            String getRulesAsString = "";
            RulesDisplayActivity.rules.clear();
            RulesDisplayActivity.rulesAsList.clear();
            outerList.clear();
            getRulesAsList.clear();

            //Getting and Parsing rules to make sense to the user
            ReceivedString = JSONHandler.ProcessListRequests(serverURL, getRulesAsList, "getRules");
            rules = ReceivedString.split("###");
            //for every rule
            for (String r : rules) {
                innerList = new ArrayList<>();
                // Finding temperature condition
                if (r.contains("freezing")) {
                    t1 = "freezing";
                } else if (r.contains("cold")) {
                    t1 = "cold";
                } else if (r.contains("comfort")) {
                    t1 = "comfort";
                } else if (r.contains("warm")) {
                    t1 = "warm";
                } else if (r.contains("hot")) {
                    t1 = "hot";
                } else {
                    t1 = "";
                }
                innerList.add(t1);

                // Finding connector condition
                if (r.contains("or")) {
                    t2 = "or";
                } else if (r.contains("and")) {
                    t2 = "and";
                } else {
                    t2 = "";
                }
                innerList.add(t2);
                // Finding ambient condition
                if (r.contains("dark")) {
                    t3 = "dark";
                } else if (r.contains("dim")) {
                    t3 = "dim";
                } else if (r.contains("bright")) {
                    t3 = "bright";
                } else {
                    t3 = "";
                }
                innerList.add(t3);
                // Finding blind condition
                if (r.contains("close")) {
                    t4 = "close";
                } else if (r.contains("half")) {
                    t4 = "half";
                } else if (r.contains("open")) {
                    t4 = "open";
                } else {
                    t4 = "";
                }
                innerList.add(t4);

                outerList.add(innerList);
            }
            ArrayList<String> newrules = new ArrayList<>();

            for (int i = 0; i < outerList.size(); i++) {
                rule = "";
                innerList = outerList.get(i);
                rule += "IF ";
                if (!innerList.get(0).equals("")) {
                    rule += innerList.get(0) + " ";
                }
                if (!innerList.get(1).equals("")) {
                    rule += innerList.get(1) + " ";
                }
                if (!innerList.get(2).equals("")) {
                    rule += innerList.get(2) + " ";
                }
                if (!innerList.get(3).equals("")) {
                    rule += "THEN " + innerList.get(3);
                }
                //yourList.add(rule);
                newrules.add(rule);
            }
            RulesDisplayActivity.rules = newrules;
            RulesDisplayActivity.rulesAsList = (ArrayList<ArrayList<String>>) outerList.clone();
        }
    }

    public String getLocalIpAddress() {
        WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }


}
