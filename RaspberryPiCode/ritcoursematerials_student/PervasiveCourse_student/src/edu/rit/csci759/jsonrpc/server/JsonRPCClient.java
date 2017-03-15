package edu.rit.csci759.jsonrpc.server;

//The Client sessions package
import java.net.MalformedURLException;
//For creating URLs
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import edu.rit.csci759.rspi.RpiIndicator;

//The JSON Smart package for JSON encoding/decoding (optional)

/**
 * class used for publishing updates to the subscribed device
 * @author aa5394
 *
 */
public class JsonRPCClient {
	Rules r;
	
	public JsonRPCClient(Rules ru) {
		this.r = ru;
	}
	
	static List<Object> list;
	public void ProcessRequest(String Server_IPAddress, String method) {
		RpiIndicator ri = new RpiIndicator();
		

		// Creating a new session to a JSON-RPC 2.0 web service at a specified
		// URL

		// The JSON-RPC 2.0 server URL
		URL serverURL = null;
		
		try {
			serverURL = new URL("http://" + Server_IPAddress);

		} catch (MalformedURLException e) {
			// handle exception...
		}

		
		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
		

		// Once the client session object is created, you can use to send a
		// series
		// of JSON-RPC 2.0 requests and notifications to it.

		// Sending an example "getTime" request:
		// Construct new request

		int requestID = 0;
		list = new ArrayList<Object>();
		list.add((Object)(""+ri.read_temperature()));
		list.add((Object)(""+ri.read_ambient_light_intensity()));
		list.add((Object)(""+r.getBlindPosition()));
		System.out.println("Sending "+list.get(0).toString() +"  "+ list.get(1).toString() + "  "+list.get(2).toString());
		
		JSONRPC2Request request = new JSONRPC2Request(method,list, requestID);
		
		// Send request
		JSONRPC2Response response = null;

		
		// NO RETURN OCCURING FOR THIS, NETWORK EXCEPTION, CONNECTION REFUSED
		
		try {
			response = mySession.send(request);
			
		} catch (JSONRPC2SessionException e) {

			
			// handle exception...
		}

		try {
			System.out.println("Checking success");
			// Print response result / error
			if (response.indicatesSuccess())
				System.out.println(response.getResult());
			else
				System.out.println(response.getError().getMessage());
		} catch (Exception e) {
			System.out.println("ERROR: in CLIENT block 2");
		}

	}
}