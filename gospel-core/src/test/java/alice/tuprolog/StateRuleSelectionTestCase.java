package alice.tuprolog;

import com.szadowsz.gospel.core.PrologEngine;
import com.szadowsz.gospel.core.Theory;
import junit.framework.TestCase;

public class StateRuleSelectionTestCase extends TestCase {
	
	public void testUnknownPredicateInQuery() throws MalformedGoalException {
		PrologEngine engine = new PrologEngine();
		TestWarningListener warningListener = new TestWarningListener();
		engine.addWarningListener(warningListener);
		String query = "p(X).";
		engine.solve(query);
		assertTrue(warningListener.warning.indexOf("p/1") > 0);
		assertTrue(warningListener.warning.indexOf("is unknown") > 0);
	}
	
	public void testUnknownPredicateInTheory() throws InvalidTheoryException, MalformedGoalException {
		PrologEngine engine = new PrologEngine();
		TestWarningListener warningListener = new TestWarningListener();
		engine.addWarningListener(warningListener);
		String theory = "p(X) :- a, b. \nb.";
		engine.setTheory(new Theory(theory));
		String query = "p(X).";
		engine.solve(query);
		assertTrue(warningListener.warning.indexOf("a/0") > 0);
		assertTrue(warningListener.warning.indexOf("is unknown") > 0);
	}

}