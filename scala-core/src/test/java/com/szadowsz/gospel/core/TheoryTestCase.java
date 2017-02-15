package com.szadowsz.gospel.core;

import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Term;
import com.szadowsz.gospel.util.exception.theory.InvalidTheoryException;
import com.szadowsz.gospel.core.db.theory.Theory;
import com.szadowsz.gospel.util.exception.solution.InvalidSolutionException;
import junit.framework.TestCase;

public class TheoryTestCase extends TestCase {

	public void testToStringWithParenthesis() throws InvalidTheoryException {
		String before = "a :- b, (d ; e).";
		Theory theory = new Theory(before);
		String after = theory.toString();
		assertEquals(theory.toString(), new Theory(after).toString());
	}
	
	public void testAppendClauseLists() throws InvalidTheoryException, InvalidSolutionException {
		Term[] clauseList = new Term[] {new Struct("p"), new Struct("q"), new Struct("r")};
		Term[] otherClauseList = new Term[] {new Struct("a"), new Struct("b"), new Struct("c")};
		Theory theory = new Theory(new Struct(clauseList));
		theory.append(new Theory(new Struct(otherClauseList)));
		Prolog engine = new Prolog();
		engine.setTheory(theory);
		assertTrue((engine.solve("p.")).isSuccess());
		assertTrue((engine.solve("b.")).isSuccess());
	}

}
