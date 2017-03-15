package edu.rit.csci759.fuzzylogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class MyTipperClass {
	public static void main(String[] args) throws Exception {
		String filename = "FuzzyLogic/tipper.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Set inputs
		fb.setVariable("temperature", 38.5);
		fb.setVariable("ambient", 27.5);

		// Evaluate
		fb.evaluate();

		// Show output variable's chart
		fb.getVariable("blinds").defuzzify();

		// Print ruleSet
		//System.out.println(fb);
		System.out.println("Blinds: " + fb.getVariable("blinds").getValue());

	}
}
