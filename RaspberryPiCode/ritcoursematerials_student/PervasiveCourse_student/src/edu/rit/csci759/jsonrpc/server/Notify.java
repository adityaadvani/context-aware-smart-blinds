package edu.rit.csci759.jsonrpc.server;

import edu.rit.csci759.rspi.RpiIndicator;

/**
 * This class runs the threads that fetches the current temperature update every
 * 1 second and Publishes the update to the Subscribed device once the
 * conditions for minimum temperature change difference has been met
 * 
 * @author aa5394
 * 
 */
public class Notify implements Runnable {
	public static int TempDiff = 2;
	static Rules r;
	static JsonRPCClient jrpc;
	static String IPAddress = "";

	// constructor instantiating an object of the class JsonRPCClient with the
	// object of class Rules as a parameter so that every class shares the same
	// instance of the Fuzzy Logic Function block
	public Notify(Rules ru) {
		r = ru;
		jrpc = new JsonRPCClient(r);
	}

	public Notify() {

	}

	// method to start the update checker thread
	public void check() {
		Thread t = new Thread(new Notify());
		t.start();
	}

	// method called by the user using the Subscribed Android device to change
	// the minimum temperature change difference required before an update is
	// published to the Subscribed device
	public static String setNotifyLimit(int n) {
		TempDiff = n;
		System.out.println("New Notification Min Change Limit set to: " + n);
		return "Notify Limit set to " + n;
	}

	// method is used to allow an Android device to reconnect with the Raspberry
	// Pi
	public static String reConnect(String IPAdd) {
		IPAddress = IPAdd + ":5050";
		System.out.println("Device re-connected, publishing update.");
		jrpc.ProcessRequest(IPAddress, "update");
		return "IP has been set";
	}

	// method is used to allow an Android device to connect for the first time
	// with the Raspberry Pi
	public static String setDeviceIP(String IPAdd) {
		IPAddress = IPAdd + ":5050";
		System.out.println("New device has subscribed, publishing update.");
		jrpc.ProcessRequest(IPAddress, "update");
		return "IP has been set";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		RpiIndicator ri = new RpiIndicator();

		int t1, t2;

		t1 = ri.read_temperature();

		while (true) {

			t2 = ri.read_temperature();
			

			if (Math.abs(t2 - t1) >= TempDiff) {
				
				System.out.println("Temperature update detected");

				if (IPAddress.equals("")) {
					System.out.println("Waiting for a device to connect.");
				} else {
					System.out
							.println("Published update to Subscriber at IP Address: "
									+ IPAddress);
					jrpc.ProcessRequest(IPAddress, "update");
				}

				t1 = t2;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}