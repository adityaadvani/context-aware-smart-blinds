package com.example.rajkoushik.testblind;

import android.app.IntentService;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import android.app.Activity;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.PendingIntent.getActivity;
//Handler for updates made from the PI and fetching the rules for setting in handling the persistent rules case.

public class JSONHandler {


    static String tempval = "", ambval = "", blindval = "", Rules = "";


    private final static String serverURL = ConnectionActivity.globalIpValue;
    static SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");

    public static String ProcessRequest(String server_URL_text, String method) {
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL


        Log.d("Debug serverURL", server_URL_text);

        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            serverURL = new URL("http://" + server_URL_text);
        } catch (MalformedURLException e) {
            // handle exception...
        }

        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

        int requestID = 0;


        JSONRPC2Request request = new JSONRPC2Request(method, requestID);

        Log.d("debug serv", "bef aft");

        // Send request
        JSONRPC2Response response = null;

        try {
            response = mySession.send(request);
        } catch (JSONRPC2SessionException e) {

        }

        if (response != null) {
            return response.getResult().toString();
        } else {
            return " Error ";
        }

    }

    public static String ProcessListRequests(String server_URL_text, List list, String method) {
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL

        Log.d("Debug serverURL1", server_URL_text);

        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            serverURL = new URL("http://" + server_URL_text);
        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

        // Once the client session object is created, you can use to send a series
        // of JSON-RPC 2.0 requests and notifications to it.

        // Sending an example "getTime" request:
        // Construct new request

        int requestID = 0;
        Log.d("Debug serve call", "before");
        JSONRPC2Request request = new JSONRPC2Request(method, list, requestID);
        Log.d("Debug serve call", "after");

        // Send request
        JSONRPC2Response response = null;

        try {
            response = mySession.send(request);

        } catch (JSONRPC2SessionException e) {

        }

        if (response != null) {
            // Print response result / error
            Log.d("runtime12", "res not null");
            if (response.indicatesSuccess())
                Log.d("debug", response.getResult().toString());
            else
                Log.e("error", response.getError().getMessage().toString());

            return response.getResult().toString();
        } else {
            return " Error ";
        }

    }

    // Implements a handler for an "echo" JSON-RPC method
    public static class UpdateHandler implements RequestHandler {

        String tempUpdate = "", ambUpdate = "", blindUpdate = "";
        List list;

        // Reports the method names of the handled requests
        public String[] handledRequests() {

            return new String[]{"update", "getRules"};
        }

        // Processes the requests
        public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

            if (req.getMethod().equals("update")) {
                list = req.getPositionalParams();
//				update valuse on textviews in the main_activity
                tempUpdate = list.get(0).toString();
                int ambval1 = Integer.parseInt(list.get(1).toString());
                String ambstat = "";
                if (0 <= ambval1 && ambval1 < 34) {
                    ambstat = "DARK";
                } else if (34 <= ambval1 && ambval1 < 67) {
                    ambstat = "DIM";
                } else {
                    ambstat = "BRIGHT";
                }
                ambUpdate = ambstat;
                blindUpdate = list.get(2).toString();
                tempval = tempUpdate;
                ambval = ambUpdate;
                blindval = blindUpdate;
                return new JSONRPC2Response("ok", req.getID());
            } else if (req.getMethod().equals("getRules")) {
                Rules = list.get(0).toString();
                return new JSONRPC2Response("ok", req.getID());
            } else {
                return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
            }
        }


    }
}
