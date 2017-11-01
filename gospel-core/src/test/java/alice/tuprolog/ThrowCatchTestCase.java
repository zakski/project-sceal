package alice.tuprolog;

import com.szadowsz.gospel.core.PrologEngine;
import com.szadowsz.gospel.core.Solution;
import com.szadowsz.gospel.core.Theory;
import com.szadowsz.gospel.core.data.Int;
import com.szadowsz.gospel.core.data.Struct;
import junit.framework.TestCase;

/**
 * @author Matteo Iuliani
 * 
 *         Test del funzionamento dei predicati throw/1 e catch/3
 */
public class ThrowCatchTestCase extends TestCase {

	// verifico che handler venga eseguito con le sostituzioni effettuate
	// nell'unificazione
	public void test_catch_3_1() throws Exception {
		PrologEngine engine = new PrologEngine();
		String theory = "p(0) :- p(1). p(1) :- throw(error).";
		engine.setTheory(new Theory(theory));
		String goal = "atom_length(err, 3), catch(p(0), E, (atom_length(E, Length), X is 2+3)), Y is X+5.";
		Solution info = engine.solve(goal);
		assertTrue(info.isSuccess());
		Struct e = (Struct) info.getTerm("E");
		assertTrue(e.isEqual(new Struct("error")));
		Int length = (Int) info.getTerm("Length");
		assertTrue(length.intValue() == 5);
		Int x = (Int) info.getTerm("X");
		assertTrue(x.intValue() == 5);
		Int y = (Int) info.getTerm("Y");
		assertTrue(y.intValue() == 10);
	}

	// verifico che venga eseguito il piu' vicino antenato catch/3 nell'albero di
	// risoluzione il cui secondo argomento unifica con l'argomento di throw/1
	public void test_catch_3_2() throws Exception {
		PrologEngine engine = new PrologEngine();
		String theory = "p(0) :- throw(error). p(1).";
		engine.setTheory(new Theory(theory));
		String goal = "catch(p(1), E, fail), catch(p(0), E, atom_length(E, Length)).";
		Solution info = engine.solve(goal);
		assertTrue(info.isSuccess());
		Struct e = (Struct) info.getTerm("E");
		assertTrue(e.isEqual(new Struct("error")));
		Int length = (Int) info.getTerm("Length");
		assertTrue(length.intValue() == 5);
	}

	// verifico che l'esecuzione fallisce se si verifica un errore durante
	// l'esecuzione di un goal e non viene trovato nessun nodo catch/3 il cui
	// secondo argomento unifica con l'argomento dell'eccezione lanciata
	public void test_catch_3_3() throws Exception {
		PrologEngine engine = new PrologEngine();
		String theory = "p(0) :- throw(error).";
		engine.setTheory(new Theory(theory));
		String goal = "catch(p(0), error(X), true).";
		Solution info = engine.solve(goal);
		assertFalse(info.isSuccess());
		assertTrue(info.isHalted());
	}

	// verifico che catch/3 fallisce se Handler e' falso
	public void test_catch_3_4() throws Exception {
		PrologEngine engine = new PrologEngine();
		String theory = "p(0) :- throw(error).";
		engine.setTheory(new Theory(theory));
		String goal = "catch(p(0), E, E == err).";
		Solution info = engine.solve(goal);
		assertFalse(info.isSuccess());
	}

	// verifico che un Goal non deterministico venga rieseguito, e nel caso
	// venga lanciata una eccezione durante l'esecuzione di Goal allora tutti i
	// punti di scelta devono essere tagliati e Handler deve essere eseguito una
	// sola volta
	public void test_catch_3_5() throws Exception {
		PrologEngine engine = new PrologEngine();
		String theory = "p(0). p(1) :- throw(error). p(2).";
		engine.setTheory(new Theory(theory));
		String goal = "catch(p(X), E, E == error).";
		Solution info = engine.solve(goal);
		assertTrue(info.isSuccess());
		assertTrue(info.hasOpenAlternatives());
		info = engine.solveNext();
		assertTrue(info.isSuccess());
		assertFalse(info.hasOpenAlternatives());
	}

	// verifico che catch/3 fallisce se si verifica un'eccezione durante Handler
	public void test_catch_3_6() throws Exception {
		PrologEngine engine = new PrologEngine();
		String theory = "p(0) :- throw(error).";
		engine.setTheory(new Theory(theory));
		String goal = "catch(p(0), E, throw(err)).";
		Solution info = engine.solve(goal);
		assertFalse(info.isSuccess());
		assertTrue(info.isHalted());
	}

}