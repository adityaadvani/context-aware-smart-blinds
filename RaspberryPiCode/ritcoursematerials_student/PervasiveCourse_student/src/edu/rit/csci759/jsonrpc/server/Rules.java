package edu.rit.csci759.jsonrpc.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;
import edu.rit.csci759.rspi.RpiIndicator;

public class Rules implements Serializable {

	RuleBlock rb;
	FunctionBlock fb;
	static ArrayList<ArrayList<String>> rules = new ArrayList<>();
	static ArrayList<ArrayList<String>> updaterules = new ArrayList<>();

	public Rules() {
		byte[] rbyte;
		String filename = "FuzzyLogic/tipper.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		this.fb = fis.getFunctionBlock(null);
		this.rb = fb.getFuzzyRuleBlock("rules");
		try {
			if (new File("./src/edu/rit/csci759/jsonrpc/server/",
					"savedrules.txt").exists()) {
				System.out.println("using existing block");
				// If server is not running for the first time and persistent
				// file exists
				if (new File("./src/edu/rit/csci759/jsonrpc/server/",
						"savedrules.txt").exists()) {
					// open file and get persistent object
					File f = new File(
							"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");
					rbyte = new byte[(int) f.length()];
					FileInputStream fileis = new FileInputStream(f);
					fileis.read(rbyte);
					fileis.close();
					rules = (ArrayList<ArrayList<String>>) deserialize(rbyte);

					// empty the rule block
					for (int i = 0; i < 8; i++) {
						Rule ru = new Rule("", this.rb);
						for (Rule r : this.rb) {
							ru = r;
							break;
						}
						this.rb.remove(ru);
					}

					// add all the stored rules to the rule block
					ArrayList<String> inner;
					RuleExpression re;
					Rule r;
					for (int i = 0; i < rules.size(); i++) {
						r = new Rule("" + (i + 1), this.rb);
						inner = rules.get(i);
						RuleTerm temperature = new RuleTerm(
								this.fb.getVariable("temperature"),
								inner.get(0), false);

						RuleTerm ambient = new RuleTerm(
								this.fb.getVariable("ambient"), inner.get(2),
								false);

						if (inner.get(1).equalsIgnoreCase("or")) {
							re = new RuleExpression(temperature, ambient,
									RuleConnectionMethodOrMax.get());
							r.setAntecedents(re);

						} else {
							re = new RuleExpression(temperature, ambient,
									RuleConnectionMethodAndMin.get());
							r.setAntecedents(re);
						}
						r.addConsequent(this.fb.getVariable("blinds"),
								inner.get(3), false);
						this.rb.add(r);
					}

					// If server is running for the first time and persistent file does not exist
				} else {
					System.out.println("using new block");
					this.rb = fb.getFuzzyRuleBlock("rules");
					// get rules from the rule block
					rules = createArrayList(this.rb);
					Object rulesobject = (Object) rules;
					
					// storing the rule block persistently in the Raspberry Pi SD card
					rbyte = serialize(rulesobject);
					System.out.println(rbyte.length);
					File file = new File(
							"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");

					FileOutputStream fos;
					try {
						fos = new FileOutputStream(
								"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");
						fos.write(rbyte);
						fos.close();
						System.out.println("written to file");

						System.out.println("written to file");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error!, using new block");
			this.rb = fb.getFuzzyRuleBlock("rules");
			e.printStackTrace();
		}

	}

	// method for creating a list of list of strings to map the rule block to a serializable object
	public static ArrayList<ArrayList<String>> createArrayList(
			RuleBlock ruleBlock) {
		ArrayList<ArrayList<String>> al = new ArrayList<>();
		ArrayList<String> inner;
		for (Rule rs : ruleBlock.getRules()) {
			String r = rs.toString().toLowerCase();
			inner = new ArrayList<>();
			String t1, t2, t3, t4;
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
			inner.add(t1);
			// Finding connector condition
			if (r.contains("or")) {
				t2 = "or";
			} else if (r.contains("and")) {
				t2 = "and";
			} else {
				t2 = "";
			}
			inner.add(t2);
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
			inner.add(t3);
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
			inner.add(t4);

			al.add(inner);
		}

		return al;
	}

	// method for getting the updated blind state
	public String getBlindPosition() {

		RpiIndicator ri = new RpiIndicator();

		String BlindState = "";
		double BlindPosition = 0;

		// Set inputs
		this.fb.setVariable("temperature", ri.read_temperature());
		this.fb.setVariable("ambient", ri.read_ambient_light_intensity());

		// Evaluate
		this.fb.evaluate();

		BlindPosition = this.fb.getVariable("blinds").getValue();

		if (0 <= BlindPosition && BlindPosition < 34) {
			BlindState = "OPEN";
			ri.led_when_high();
		} else if (34 <= BlindPosition && BlindPosition < 67) {
			BlindState = "HALF";
			ri.led_when_mid();
		} else if (67 <= BlindPosition && BlindPosition <= 100) {
			BlindState = "CLOSE";
			ri.led_when_low();
		}

		return "" + BlindState;
	}

	// method for adding a new rule to the rule block
	public String addRule() {

		RpiIndicator ri = new RpiIndicator();

		String BlindState = "";
		double BlindPosition = 0;
		byte[] rbyte;

		RuleExpression re;

		// find next available name for the rule
		int namer = 1;
		for (Rule rr : this.rb.getRules()) {
			for (Rule r : this.rb.getRules()) {
				if (r.getName().equals("" + namer)) {
					namer++;
				}
			}
		}
		Rule r = new Rule(("" + namer), this.rb);

		// Check if rule exists
		boolean t1 = false, t2 = false, t3 = false, t4 = false;
		Rule rout = new Rule("", this.rb);
		for (Rule rin : this.rb.getRules()) {
			t1 = false;
			t2 = false;
			t3 = false;
			t4 = false;
			String rs = rin.toString().toLowerCase();
			t1 = rs.contains(JsonHandler.addlist.get(0).toString()
					.toLowerCase());
			t2 = rs.contains(JsonHandler.addlist.get(1).toString()
					.toLowerCase());
			t3 = rs.contains(JsonHandler.addlist.get(2).toString()
					.toLowerCase());
			t4 = rs.contains(JsonHandler.addlist.get(3).toString()
					.toLowerCase());
			if (t1 && t2 && t3 && t4) {
				break;
			}
		}

		// if rule does not exist, add the rule.
		if ((t1 && t2 && t3 && t4) == false) {

			RuleTerm temperature = new RuleTerm(
					this.fb.getVariable("temperature"), JsonHandler.addlist
							.get(0).toString(), false);

			RuleTerm ambient = new RuleTerm(this.fb.getVariable("ambient"),
					JsonHandler.addlist.get(2).toString(), false);

			if (JsonHandler.addlist.get(1).toString().equalsIgnoreCase("or")) {
				re = new RuleExpression(temperature, ambient,
						RuleConnectionMethodOrMax.get());
				r.setAntecedents(re);

			} else {
				re = new RuleExpression(temperature, ambient,
						RuleConnectionMethodAndMin.get());
				r.setAntecedents(re);
			}
			r.addConsequent(this.fb.getVariable("blinds"), JsonHandler.addlist
					.get(3).toString(), false);
			this.rb.add(r);

			// store RuleBlock persistently
			rules = createArrayList(this.rb);
			Object rulesobject = (Object) rules;
			rbyte = serialize(rulesobject);
			try {
				File file = new File(
						"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");

				FileOutputStream fos = new FileOutputStream(
						"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");
				fos.write(rbyte);
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Evaluate
		this.fb.evaluate();

		System.out.println(this.rb);

		BlindPosition = this.fb.getVariable("blinds").getValue();

		if (0 <= BlindPosition && BlindPosition < 34) {
			BlindState = "OPEN";
			ri.led_when_high();
		} else if (34 <= BlindPosition && BlindPosition < 67) {
			BlindState = "HALF";
			ri.led_when_mid();
		} else if (67 <= BlindPosition && BlindPosition <= 100) {
			BlindState = "CLOSE";
			ri.led_when_low();
		}

		return "" + BlindState;
	}

	// method for removing a rule from the rule block
	public String removeRule() {

		RpiIndicator ri = new RpiIndicator();

		String BlindState = "";
		double BlindPosition = 0;
		byte[] rbyte;

		// identifying the rule
		boolean t1 = false, t2 = false, t3 = false, t4 = false;
		Rule re = new Rule("", this.rb);
		for (Rule r : this.rb.getRules()) {
			t1 = false;
			t2 = false;
			t3 = false;
			t4 = false;
			String rs = r.toString().toLowerCase();
			t1 = rs.contains(JsonHandler.addlist.get(0).toString()
					.toLowerCase());
			t2 = rs.contains(JsonHandler.addlist.get(1).toString()
					.toLowerCase());
			t3 = rs.contains(JsonHandler.addlist.get(2).toString()
					.toLowerCase());
			t4 = rs.contains(JsonHandler.addlist.get(3).toString()
					.toLowerCase());
			if (t1 && t2 && t3 && t4) {
				re = r;
				break;
			}
		}

		// if rule is found, remove it
		if (t1 && t2 && t3 && t4) {
			this.rb.remove(re);

			// storing updated RuleBlock persistently
			rules = createArrayList(this.rb);
			Object rulesobject = (Object) rules;
			rbyte = serialize(rulesobject);
			try {
				File file = new File(
						"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");

				FileOutputStream fos = new FileOutputStream(
						"./src/edu/rit/csci759/jsonrpc/server/savedrules.txt");
				fos.write(rbyte);
				fos.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Evaluate and update blind state
		this.fb.evaluate();

		System.out.println(this.rb);

		BlindPosition = this.fb.getVariable("blinds").getValue();

		if (0 <= BlindPosition && BlindPosition < 34) {
			BlindState = "OPEN";
			ri.led_when_high();
		} else if (34 <= BlindPosition && BlindPosition < 67) {
			BlindState = "HALF";
			ri.led_when_mid();
		} else if (67 <= BlindPosition && BlindPosition <= 100) {
			BlindState = "CLOSE";
			ri.led_when_low();
		}

		return "" + BlindState;

	}

	// method for getting rules back in the form of a single string
	public String getRules() {
		String rules = "";
		String rs;
		for (Rule rin : this.rb.getRules()) {
			rs = rin.toString().toLowerCase();
			rules += rs + "###";
		}

		return rules;
	}

	// method for converting object into byte array
	public static byte[] serialize(Object r) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(out);
		} catch (IOException e) {
			System.out.println("In Serialize Rules.java");
			e.printStackTrace();
		}
		try {
			os.writeObject(r);
		} catch (IOException e) {
			System.out.println("In Serialize Rules.java");
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	// method for converting byte array into object
	public static Object deserialize(byte[] rb) {
		ByteArrayInputStream in = new ByteArrayInputStream(rb);
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(in);
		} catch (IOException e) {
			System.out.println("In Serialize Rules.java");
			e.printStackTrace();
		}
		try {
			return is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("In Serialize Rules.java");
			e.printStackTrace();
		}
		return is;
	}

}