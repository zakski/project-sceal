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
package com.szadowsz.gospel.core.db;

import alice.tuprolog.IPrimitives;
import com.szadowsz.gospel.core.PrologEngine;
import com.szadowsz.gospel.core.data.Number;
import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Term;
import com.szadowsz.gospel.core.db.primitives.PrimitiveInfo;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This abstract class is the base class for developing
 * tuProlog built-in libraries, which can be dynamically
 * loaded by prolog objects.
 * <p>
 * Each library can expose to engine:
 * <ul>
 * <li> a theory (as a string assigned to theory field)
 * <li> builtin predicates: each method whose signature is
 * boolean name_arity(Term arg0, Term arg1,...)
 * is considered a built-in predicate provided by the library
 * <li> builtin evaluable functors: each method whose signature is
 * Term name_arity(Term arg0, Term arg1,...)
 * is considered a built-in functors provided by the library
 * </ul>
 * <p>
 */
public abstract class Library implements Serializable, IPrimitives {
    private static final long serialVersionUID = 1L;
    /**
     * prolog core which loaded the library
     */
    protected PrologEngine engine;

    /**
     * operator mapping
     */
    private final String[][] opMappingCached;

    public Library() {
        opMappingCached = getSynonymMap();
    }

    /**
     * Gets the name of the library.
     * <p>
     * By default the name is the class name.
     *
     * @return the library name
     */
    public String getName() {
        return getClass().getName();
    }

    /**
     * Gets the theory provided with the library
     * <p>
     * Empty theory is provided by default.
     */
    public String getTheory() {
        return "";
    }

    public String getTheory(int a) {
        return "";
    }

    /**
     * Gets the synonym mapping, as array of
     * elements like  { synonym, original name}
     */
    protected String[][] getSynonymMap() {
        return null;
    }

    /**
     * Gets the engine to which the library is bound
     *
     * @return the engine
     */
    public PrologEngine getEngine() {
        return engine;
    }

    /**
     * @param en
     */
    public void setEngine(PrologEngine en) {
        engine = en;
    }

    /**
     * tries to unify two terms
     * <p>
     * The runtime (demonstration) context currently used by the engine
     * is deployed and altered.
     */
    protected boolean unify(Term a0, Term a1) {
        return engine.unify(a0, a1);
    }

    /**
     * tries to unify two terms
     * <p>
     * The runtime (demonstration) context currently used by the engine
     * is deployed and altered.
     */
    protected boolean match(Term a0, Term a1) {
        return engine.match(a0, a1);
    }


    /**
     * Evaluates an expression. Returns null value if the argument
     * is not an evaluable expression
     * <p>
     * The runtime (demo) context currently used by the engine
     * is deployed and altered.
     *
     * @throws Throwable
     */
    protected Term evalExpression(Term term) throws Throwable {
        if (term == null)
            return null;
        Term val = term.getTerm();
        if (val instanceof Struct) {
            Struct t = (Struct) val;
            if (term != t)
                if (!t.isPrimitive())
                    engine.identifyFunctor(t);
            if (t.isPrimitive()) {
                PrimitiveInfo bt = t.getPrimitive();
                // check for library functors
                if (bt.isFunctor())
                    return bt.evalAsFunctor(t);
            }
        } else if (val instanceof Number) {
            return val;
        }
        return null;
    }


    /**
     * method invoked by prolog engine when library is
     * going to be removed
     */
    public void dismiss() {
    }

    /**
     * method invoked when the engine is going
     * to demonstrate a goal
     */
    public void onSolveBegin(Term goal) {
    }

    /**
     * method invoked when the engine has
     * finished a demostration
     */

    public void onSolveHalt() {
    }

    public void onSolveEnd() {
    }

    /**
     * gets the list of predicates defined in the library
     */
    public Map<Integer, List<PrimitiveInfo>> getPrimitives() {
        try {
            java.lang.reflect.Method[] mlist = this.getClass().getMethods();
            Map<Integer, List<PrimitiveInfo>> mapPrimitives = new HashMap<>();
            mapPrimitives.put(PrimitiveInfo.DIRECTIVE, new ArrayList<>());
            mapPrimitives.put(PrimitiveInfo.FUNCTOR, new ArrayList<>());
            mapPrimitives.put(PrimitiveInfo.PREDICATE, new ArrayList<>());
            //{new ArrayList<PrimitiveInfo>(), new ArrayList<PrimitiveInfo>(), new ArrayList<PrimitiveInfo>()};

            for (Method aMlist : mlist) {
                String name = aMlist.getName();

                Class<?>[] clist = aMlist.getParameterTypes();
                Class<?> rclass = aMlist.getReturnType();
                String returnTypeName = rclass.getName();

                int type;
                if (returnTypeName.equals("boolean")) type = PrimitiveInfo.PREDICATE;
                else if (returnTypeName.equals("com.szadowsz.gospel.core.data.Term")) type = PrimitiveInfo.FUNCTOR;
                else if (returnTypeName.equals("void")) type = PrimitiveInfo.DIRECTIVE;
                else continue;

                int index = name.lastIndexOf('_');
                if (index != -1) {
                    try {
                        int arity = Integer.parseInt(name.substring(index + 1, name.length()));
                        // check arg number
                        if (clist.length == arity) {
                            boolean valid = true;
                            for (int j = 0; j < arity; j++) {
                                if (!(Term.class.isAssignableFrom(clist[j]))) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (valid) {
                                String rawName = name.substring(0, index);
                                String key = rawName + "/" + arity;
                                PrimitiveInfo prim = new PrimitiveInfo(type, key, this, aMlist, arity);
                                mapPrimitives.get(type).add(prim);
                                //
                                // adding also or synonims
                                //
                                String[] stringFormat = {"directive", "predicate", "functor"};
                                if (opMappingCached != null) {
                                    for (String[] map : opMappingCached) {
                                        if (map[2].equals(stringFormat[type]) && map[1].equals(rawName)) {
                                            key = map[0] + "/" + arity;
                                            prim = new PrimitiveInfo(type, key, this, aMlist, arity);
                                            mapPrimitives.get(type).add(prim);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }

            }
            return mapPrimitives;
        } catch (Exception ex) {
            return null;
        }
    }
}