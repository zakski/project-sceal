package alice.tuprolog;

import com.szadowsz.gospel.core.PrologEngine;
import com.szadowsz.gospel.core.Solution;
import com.szadowsz.gospel.core.Theory;
import com.szadowsz.gospel.core.data.Int;
import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Term;
import com.szadowsz.gospel.core.data.Var;
import com.szadowsz.gospel.core.db.libs.BuiltIn;
import com.szadowsz.gospel.core.error.InvalidTermException;
import com.szadowsz.gospel.core.error.InvalidTheoryException;
import com.szadowsz.gospel.core.error.MalformedGoalException;
import junit.framework.TestCase;

public class BuiltInTestCase extends TestCase {
	
	public void testConvertTermToGoal() throws InvalidTermException {
		Term t = new Var("T");
		Struct result = new Struct("call", t);
		assertEquals(result, BuiltIn.convertTermToGoal(t));
		assertEquals(result, BuiltIn.convertTermToGoal(new Struct("call", t)));
		
		t = new Int(2);
		assertNull(BuiltIn.convertTermToGoal(t));
		
		t = new Struct("p", new Struct("a"), new Var("B"), new Struct("c"));
		result = (Struct) t;
		assertEquals(result, BuiltIn.convertTermToGoal(t));
		
		Var linked = new Var("X");
		linked.setLink(new Struct("!"));
		Term[] arguments = new Term[] { linked, new Var("Y") };
		Term[] results = new Term[] { new Struct("!"), new Struct("call", new Var("Y")) };
		assertEquals(new Struct(";", results), BuiltIn.convertTermToGoal(new Struct(";", arguments)));
		assertEquals(new Struct(",", results), BuiltIn.convertTermToGoal(new Struct(",", arguments)));
		assertEquals(new Struct("->", results), BuiltIn.convertTermToGoal(new Struct("->", arguments)));
	}
	
	//Based on the bug #59 Grouping conjunctions in () changes result on sourceforge
	public void testGroupingConjunctions() throws InvalidTheoryException, MalformedGoalException {
		PrologEngine engine = new PrologEngine();
		engine.setTheory(new Theory("g1. g2."));
		Solution info = engine.solve("(g1, g2), (g3, g4).");
		assertFalse(info.isSuccess());
		engine.setTheory(new Theory("g1. g2. g3. g4."));
		info = engine.solve("(g1, g2), (g3, g4).");
		assertTrue(info.isSuccess());
	}

}
