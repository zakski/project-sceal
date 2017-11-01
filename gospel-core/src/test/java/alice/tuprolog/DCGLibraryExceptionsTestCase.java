package alice.tuprolog;

import com.szadowsz.gospel.core.PrologEngine;
import com.szadowsz.gospel.core.Solution;
import com.szadowsz.gospel.core.data.Int;
import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Var;
import junit.framework.TestCase;

/**
 * @author Matteo Iuliani
 * 
 *         Test del funzionamento delle eccezioni lanciate dai predicati della
 *         DCGLibrary
 */
public class DCGLibraryExceptionsTestCase extends TestCase {

	// verifico che phrase(X, []) lancia un errore di instanziazione
	public void test_phrase_2_1() throws Exception {
		PrologEngine engine = new PrologEngine();
		engine.loadLibrary("alice.tuprolog.lib.DCGLibrary");
		String goal = "catch(phrase(X, []), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
		Solution info = engine.solve(goal);
		assertTrue(info.isSuccess());
		Struct g = (Struct) info.getTerm("Goal");
		assertTrue(g.isEqual(new Struct("phrase_guard", new Var("X"),
				new Struct())));
		Int argNo = (Int) info.getTerm("ArgNo");
		assertTrue(argNo.intValue() == 1);
	}

	// verifico che phrase(X, [], []) lancia un errore di instanziazione
	public void test_phrase_3_1() throws Exception {
		PrologEngine engine = new PrologEngine();
		engine.loadLibrary("alice.tuprolog.lib.DCGLibrary");
		String goal = "catch(phrase(X, [], []), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
		Solution info = engine.solve(goal);
		assertTrue(info.isSuccess());
		Struct g = (Struct) info.getTerm("Goal");
		assertTrue(g.isEqual(new Struct("phrase_guard", new Var("X"),
				new Struct(), new Struct())));
		Int argNo = (Int) info.getTerm("ArgNo");
		assertTrue(argNo.intValue() == 1);
	}

}