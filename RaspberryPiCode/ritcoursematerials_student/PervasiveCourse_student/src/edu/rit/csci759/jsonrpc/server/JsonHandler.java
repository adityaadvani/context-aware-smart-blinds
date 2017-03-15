package edu.rit.csci759.jsonrpc.server;

/**
 * Demonstration of the JSON-RPC 2.0 Server framework usage. The request
 * handlers are implemented as static nested classes for convenience, but in 
 * real life applications may be defined as regular classes within their old 
 * source files.
 *
 * @author Vladimir Dzhuvinov
 * @version 2011-03-05
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import edu.rit.csci759.rspi.RpiIndicator;

public class JsonHandler {
	public static List addlist;

	// Implements a handler for an "echo" JSON-RPC method
	public static class EchoHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "echo" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			if (req.getMethod().equals("echo")) {

				// Echo first parameter

				List params = (List) req.getParams();

				Object input = params.get(0);

				return new JSONRPC2Response(input, req.getID());
			} else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	// Implements a handler for "getDate" and "getTime" JSON-RPC methods
	// that return the current date and time
	public static class SmartBlindApp implements RequestHandler {

		Rules r;

		public SmartBlindApp(Rules ru) {
			this.r = ru;
		}

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "getDate", "getTime", "read_temperature",
					"read_ambient_light_intensity", "blindState", "addRule",
					"setNotifyLimit", "removeRule", "setIP", "getRules",
					"reConnect" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			// To set the dynamic IP of the connected device
			if (req.getMethod().equals("setIP")) {
				addlist = req.getPositionalParams();
				return new JSONRPC2Response(Notify.setDeviceIP(addlist.get(0)
						.toString()), req.getID());

				// To reset the dynamic IP to reconnect the device
			} else if (req.getMethod().equals("reConnect")) {
				addlist = req.getPositionalParams();
				return new JSONRPC2Response(Notify.setDeviceIP(addlist.get(0)
						.toString()), req.getID());

				// To get time from RPi
			} else if (req.getMethod().equals("getDate")) {
				DateFormat df = DateFormat.getDateInstance();
				String date = df.format(new Date());
				return new JSONRPC2Response(hostname + " " + date, req.getID());

				// To get time from RPi
			} else if (req.getMethod().equals("getTime")) {
				System.out.println("Got request for time");
				DateFormat df = DateFormat.getTimeInstance();
				String time = df.format(new Date());
				System.out.println("Returning: " + time);
				return new JSONRPC2Response(hostname + " " + time, req.getID());

				// To get temperature from RPi
			} else if (req.getMethod().equals("read_temperature")) {
				System.out.println("Got request for temperature");
				RpiIndicator ri = new RpiIndicator();
				int temp = ri.read_temperature();
				System.out.println("Returning " + temp);
				return new JSONRPC2Response(" " + temp, req.getID());

				// To get ambient from RPi
			} else if (req.getMethod().equals("read_ambient_light_intensity")) {
				System.out.println("Got request for amb");
				RpiIndicator ri = new RpiIndicator();
				int amb = ri.read_ambient_light_intensity();
				if (amb < 34) {
					System.out.println("Returning DARK");
					return new JSONRPC2Response("DARK", req.getID());
				} else if (34 <= amb && amb < 67) {
					System.out.println("Returning DIM");
					return new JSONRPC2Response("DIM", req.getID());
				} else if (amb >= 67) {
					System.out.println("Returning BRIGHT");
					return new JSONRPC2Response("BRIGHT", req.getID());
				}
				System.out.println("Returning BLANK");
				return new JSONRPC2Response(" ", req.getID());

				// To get blind's status from RPi
			} else if (req.getMethod().equals("blindState")) {
				System.out.println("Got request for blind");
				String blindpos = r.getBlindPosition();
				System.out.println("Returning " + blindpos);
				return new JSONRPC2Response(blindpos, req.getID());

				// To add a new rule to RPi FuzzyLogic
			} else if (req.getMethod().equals("addRule")) {
				addlist = req.getPositionalParams();
				System.out.println("Received rule temperature: "
						+ addlist.get(0));
				System.out
						.println("Received rule connector: " + addlist.get(1));
				System.out.println("Received rule ambient: " + addlist.get(2));
				System.out.println("Received rule blindstatus: "
						+ addlist.get(3));
				return new JSONRPC2Response(r.addRule(), req.getID());

				// To remove a rule from RPi FuzzyLogic
			} else if (req.getMethod().equals("removeRule")) {
				addlist = req.getPositionalParams();
				return new JSONRPC2Response(r.removeRule(), req.getID());

				// To change minimum temperature change requirement for
				// publishing an update to the subscribed device
			} else if (req.getMethod().equals("setNotifyLimit")) {
				addlist = req.getPositionalParams();
				return new JSONRPC2Response(Notify.setNotifyLimit(Integer
						.parseInt(addlist.get(0).toString())), req.getID());

				// To get all the fuzzy rules back in form of a single string
			} else if (req.getMethod().equals("getRules")) {
				return new JSONRPC2Response(r.getRules().toString(),
						req.getID());

			} else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}
}