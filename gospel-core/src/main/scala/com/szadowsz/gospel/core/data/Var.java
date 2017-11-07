/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.szadowsz.gospel.core.data;

import com.szadowsz.gospel.core.error.InvalidTermException;

import java.util.AbstractMap;
import java.util.List;

/**
 * This class represents a variable term.
 * Variables are identified by a name (which must starts with
 * an upper case letter) or the anonymous ('_') name.
 *
 * @see Term
 */
public class Var extends Term {

    /* Identify kind of renaming */
    private final static int ORIGINAL = -1;
    public final static int PROGRESSIVE = -2;
    private final static String ANY = "_";
    private static final long serialVersionUID = 1L;
    private static long fingerprint = 0; //Alberto //static version as global counter
    @SuppressWarnings("unused")
    private String type = "Var";
    // the name identifying the var
    private String name;
    private StringBuilder completeName;    /* Reviewed by Paolo Contessi */
    private Term link;                   /* link is used for unification process */
    private long internalTimestamp;      /* internalTimestamp is used for fix vars order (resolveTerm()) */
    private int ctxid;                  /* id of ExecCtx owners of this var util for renaming*/

    //Alberto
    private long fingerPrint; //fingerPrint is a unique id (per run) used for var comparison

    /**
     * Creates a variable identified by a name.
     * <p>
     * The name must starts with an upper case letter or the underscore. If an underscore is
     * specified as a name, the variable is anonymous.
     *
     * @param n is the name
     * @throws InvalidTermException if n is not a valid Prolog variable name
     */
    public Var(String n) {
        link = null;
        ctxid = Var.ORIGINAL; //no execCtx owners
        internalTimestamp = 0;
        fingerPrint = getFingerprint();
        if (n.equals(ANY)) {
            name = null;
            completeName = new StringBuilder();
        } else if (Character.isUpperCase(n.charAt(0)) ||
                (n.startsWith(ANY))) {
            name = n;
            completeName = new StringBuilder(n);
        } else {
            throw new InvalidTermException("Illegal variable name: " + n);
        }
    }

    /**
     * Creates an anonymous variable
     * <p>
     * This is equivalent to build a variable with name _
     */
    public Var() {
        name = null;
        completeName = new StringBuilder();
        link = null;
        ctxid = Var.ORIGINAL;
        internalTimestamp = 0;
        fingerPrint = getFingerprint();
    }

    /**
     * Creates a internal engine variable.
     *
     * @param n        is the name
     * @param id       is the id of ExecCtx
     * @param alias    code to discriminate external vars
     * @param isCyclic
     * @param time     is timestamp
     */
    private Var(String n, int id, int alias, long count/*, boolean isCyclic*/) {
        name = n;
        completeName = new StringBuilder();
        internalTimestamp = count;
        //this.isCyclic = isCyclic;
        fingerPrint = getFingerprint();
        link = null;
        if (id < 0) id = Var.ORIGINAL;
        rename(id, alias);
    }

    //Alberto
    private static long getFingerprint() { //called by Var constructors
        fingerprint++;
        return fingerprint;
    }

    /**
     * De-unify the variables of list
     */
    public static void free(List<Var> varsUnified) {
        for (Var v : varsUnified) {
            v.free();
        }
    }

    /**
     * Rename variable (assign completeName)
     */
    void rename(int idExecCtx, int count) { /* Reviewed by Paolo Contessi */
        ctxid = idExecCtx;

        if (ctxid > Var.ORIGINAL) {
            completeName = completeName
                    .delete(0, completeName.length())
                    .append(name).append("_e").append(ctxid);
        } else if (ctxid == ORIGINAL) {
            completeName = completeName
                    .delete(0, completeName.length())
                    .append(name);
        } else if (ctxid == PROGRESSIVE) {
            completeName = completeName
                    .delete(0, completeName.length())
                    .append("_").append(count);
        }
    }

    /**
     * Gets a copy of this variable.
     * <p>
     * if the variable is not present in the list passed as argument,
     * a copy of this variable is returned and added to the list. If instead
     * a variable with the same time identifier is found in the list,
     * then the variable in the list is returned.
     */
    @Override
    public Term copy(AbstractMap<Var, Var> vMap, int idExecCtx) {
        Term tt = getTerm();
        if (tt == this) {
            Var v = vMap.computeIfAbsent(this, k -> new Var(name, idExecCtx, 0, internalTimestamp/*, this.isCyclic*/));
            //No occurence of v before
            return v;
        } else {
            return tt.copy(vMap, idExecCtx);
        }
    }

    @Override //Alberto
    public Term copyAndRetainFreeVar(AbstractMap<Var, Var> vMap, int idExecCtx) {
        Term tt = getTerm();
        if (tt == this) {
            Var v = vMap.computeIfAbsent(this, k -> this);
            //No occurence of v before
            //!!!
            return v;
        } else {
            return tt.copy(vMap, idExecCtx);
        }
    }

    /**
     * Gets a copy of this variable.
     */
    @Override
    public Term copy(AbstractMap<Var, Var> vMap, AbstractMap<Term, Var> substMap) {
        Var v;
        Object temp = vMap.get(this);
        if (temp == null) {
            v = new Var(null, Var.PROGRESSIVE, vMap.size(), internalTimestamp/*, this.isCyclic*/);
            vMap.put(this, v);
        } else {
            v = (Var) temp;
        }

        //if(v.isCyclic) //Alberto
        //	return v;

        Term t = getTerm();
        if (t instanceof Var) {
            Object tt = substMap.get(t);
            if (tt == null) {
                substMap.put(t, v);
                v.link = null;
            } else {
                v.link = (tt != v) ? (Var) tt : null;
            }
        }
        if (t instanceof Struct) {
            v.link = t.copy(vMap, substMap);
        }
        if (t instanceof Number) v.link = t;
        return v;
    }

    /**
     * De-unify the variable
     */
    public void free() {
        link = null;
    }

    /**
     * Gets the name of the variable
     */
    public String getName() {
        if (name != null) {
            return completeName.toString();
        } else {
            return ANY;
        }
    }

    public void setName(String s) {
        this.name = s;
    }

    /**
     * Gets the name of the variable
     */
    public String getOriginalName() {
        if (name != null) {
            return name;
        } else {
            return ANY + "" + this.fingerPrint; //Alberto
        }
    }

    /**
     * Gets the term which is referred by the variable.
     * <p>
     * For unbound variable it is the variable itself, while
     * for bound variable it is the bound term.
     */
    public Term getTerm() {
        Term tt = this;
        Term t = link;
        while (t != null) {
            tt = t;
            if (t instanceof Var) {
                t = ((Var) t).link;
            } else {
                break;
            }
        }
        return tt;
    }

    /**
     * Gets the term which is direct referred by the variable.
     */
    public Term getLink() {
        return link;
    }

    /**
     * Set the term which is direct bound
     */
    public void setLink(Term l) {
        link = l;
    }

    /**
     * Set the timestamp
     */
    void setInternalTimestamp(long t) {
        internalTimestamp = t;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isStruct() {
        return false;
    }

    public boolean isVar() {
        return true;
    }

    public boolean isEmptyList() {
        Term t = getTerm();
        return t != this && t.isEmptyList();
    }

    public boolean isAtomic() {
        Term t = getTerm();
        return t != this && t.isAtomic();
    }

    public boolean isCompound() {
        Term t = getTerm();
        return t != this && t.isCompound();
    }

    public boolean isAtom() {
        Term t = getTerm();
        return t != this && t.isAtom();
    }

    public boolean isList() {
        Term t = getTerm();
        return t != this && t.isList();
    }

    public boolean isGround() {
        Term t = getTerm();
        return t != this && t.isGround();
    }

    /**
     * Tests if this variable is ANY
     */
    public boolean isAnonymous() {
        return name == null;
    }

    /**
     * Tests if this variable is bound
     */
    public boolean isBound() {
        return link != null;
    }

    /**
     * finds var occurence in a Struct, doing occur-check.
     * (era una findIn)
     *
     * @param vl     TODO
     * @param choice
     */
    private boolean occurCheck(List<Var> vl, Struct t) {
        int arity = t.getArity();
        for (int c = 0; c < arity; c++) {
            Term at = t.getTerm(c);
            if (at instanceof Struct) {
                if (occurCheck(vl, (Struct) at)) {
                    return true;
                }
            } else if (at instanceof Var) {
                Var v = (Var) at;
                if (v.link == null) {
                    vl.add(v);
                }
                if (this == v) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Resolve the occurence of variables in a Term
     */
    public long resolveTerm(long count) {
        Term tt = getTerm();
        if (tt != this) {
            return tt.resolveTerm(count);
        } else {
            internalTimestamp = count;
            return ++count;
        }
    }

    /**
     * var unification.
     * <p>
     * First, verify the Term eventually already unified with the same Var
     * if the Term exist, unify var with that term, in order to handle situation
     * as (A = p(X) , A = p(1)) which must produce X/1.
     * <p>
     * If instead the var is not already unified, then:
     * <p>
     * if the Term is a var bound to X, then try unification with X
     * so for example if A=1, B=A then B is unified to 1 and not to A
     * (note that it's coherent with chronological backtracking:
     * the eventually backtracked A unification is always after
     * backtracking of B unification.
     * <p>
     * if are the same Var, unification must succeed, but without any new
     * bindings (to avoid cycles for extends in A = B, B = A)
     * <p>
     * if the term is a number, then it's a success and new link is created
     * (retractable by means of a code)
     * <p>
     * if the term is a compound, then occur check test is executed:
     * the var must not appear in the compound ( avoid X=p(X),
     * or p(X,X)=p(Y,f(Y)) ); if occur check is ok
     * then it's success and a new link is created (retractable by a code)
     * (test done if occursCheck is enabled)
     */
    public boolean unify(List<Var> vl1, List<Var> vl2, Term t, boolean isOccursCheckEnabled) {
        Term tt = getTerm();
        if (tt == this) {
            t = t.getTerm();
            if (t instanceof Var) {
                ((Var) t).fingerPrint = this.fingerPrint; //Alberto
                if (this == t) {
                    try {
                        vl1.add(this);
                    } catch (NullPointerException e) {
                    }
                    return true;
                }
            } else if (t instanceof Struct) {
                if (isOccursCheckEnabled) {
                    if (occurCheck(vl2, (Struct) t)) {
                        //this.isCyclic = true;  //Alberto -> da usare quando si supporteranno i termini ciclici
                        return false; // da togliere
                    }
                } else {
                    checkVar(vl2, t); //Alberto
                }
            } else if (!(t instanceof Number)) {
                return false;
            }
            link = t;
            try {
                vl1.add(this);
            } catch (NullPointerException e) {
            }
            return true;
        } else {
            return (tt.unify(vl1, vl2, t, isOccursCheckEnabled));
        }
    }

    //Alberto
    private void checkVar(List<Var> vl, Term t) {
        Struct st = (Struct) t;
        int arity = st.getArity();
        for (int c = 0; c < arity; c++) {
            Term at = st.getTerm(c);
            if (at instanceof Var) {
                Var v = (Var) at;
                if (v.link == null) {
                    vl.add(v);
                }
            } else if (at instanceof Struct) {
                checkVar(vl, at);
            }
        }
    }

    public boolean isGreater(Term t) {
        Term tt = getTerm();
        if (tt == this) {
            t = t.getTerm();
            return (t instanceof Var) && fingerPrint > ((Var) t).fingerPrint; //Alberto
        } else {
            return tt.isGreater(t);
        }
    }

    /**
     * Gets the string representation of this variable.
     * <p>
     * For bounded variables, the string is <Var Name>/<bound Term>.
     */
    @Override
    public String toString() {
        Term tt = getTerm();
        if (name != null) {
            if (tt == this/* || this.isCyclic*/) {
                //if(this.isCyclic) //Alberto
                // return name;
                return completeName.toString();
            } else {
                return (completeName.toString() + " / " + tt.toString());
            }
        } else {
            if (tt == this /*|| this.isCyclic*/) {
                return ANY + "" + this.fingerPrint; //Alberto
            } else {
                return tt.toString();
            }
        }
    }


    /**
     * Gets the string representation of this variable, providing
     * the string representation of the linked term in the case of
     * bound variable
     */
    public String toStringFlattened() {
        Term tt = getTerm();
        if (name != null) {
            if (tt == this /*|| this.isCyclic*/) {
                //if(this.isCyclic)
                // return name;
                return completeName.toString();
            } else {
                return tt.toString();
            }
        } else {
            if (tt == this /*|| this.isCyclic*/) {
                return ANY + "" + this.fingerPrint; //Alberto
            } else {
                return tt.toString();
            }
        }
    }

    /*Castagna 06/2011*/
    @Override
    public void accept(TermVisitor tv) {
        tv.visit(this);
    }
}