/*
 * SolveInfo.java
 *
 * Created on 13 marzo 2007, 12.00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.engine;

import alice.tuprologx.pj.model.Term;
import com.szadowsz.gospel.core.Solution;
import com.szadowsz.gospel.core.error.NoSolutionException;
import com.szadowsz.gospel.core.error.UnknownVarException;

import java.util.List;
import java.util.Vector;

/**
 * @author Maurizio
 */
public class PrologSolution<Q extends Term<?>, S extends Term<?>> /*implements ISolution<Q,S,Term<?>>*/ {

    private final Solution _solveInfo;

    /**
     * Creates a new instance of SolveInfo
     */
    public PrologSolution(Solution si) {
        _solveInfo = si;
    }

    public <Z extends Term<?>> Z getVarValue(String varName) throws com.szadowsz.gospel.core.error.NoSolutionException {
        alice.tuprolog.Term retValue;
        retValue = _solveInfo.getVarValue(varName);
        return Term.unmarshal(retValue);
    }

    public <Z extends Term<?>> Z getTerm(String varName) throws NoSolutionException, UnknownVarException {
        alice.tuprolog.Term retValue;
        retValue = _solveInfo.getTerm(varName);
        return Term.unmarshal(retValue);
    }

    public boolean isSuccess() {
        return _solveInfo.isSuccess();
    }

    public boolean isHalted() {
        return _solveInfo.isHalted();
    }

    public boolean hasOpenAlternatives() {
        return _solveInfo.hasOpenAlternatives();
    }

    public S getSolution() throws NoSolutionException {
        alice.tuprolog.Term retValue;
        retValue = _solveInfo.getSolution();
        return Term.unmarshal(retValue);
    }

    public Q getQuery() {
        alice.tuprolog.Term retValue;
        retValue = _solveInfo.getQuery();
        return Term.unmarshal(retValue);
    }

    public List<Term<?>> getBindingVars() throws NoSolutionException {
        List<alice.tuprolog.Var> retValue;
        retValue = _solveInfo.getBindingVars();
        Vector<Term<?>> bindings = new Vector<>();
        for (alice.tuprolog.Term t : retValue) {
            bindings.add(Term.unmarshal(t));
        }
        return bindings;
    }
}
