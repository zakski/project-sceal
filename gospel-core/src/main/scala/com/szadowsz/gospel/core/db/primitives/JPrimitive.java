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
package com.szadowsz.gospel.core.db.primitives;

import alice.tuprolog.IPrimitives;
import com.szadowsz.gospel.core.db.JLibrary;
import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Term;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Primitive class
 * referring to a builtin predicate or functor
 *
 * @see Struct
 */
public final class JPrimitive implements PrimitiveInfo {

     private int type;
    /**
     * method to be call when evaluating the built-in
     */
    private Method method;
    /**
     * lib object where the builtin is defined
     */
    private IPrimitives source;
    /**
     * for optimization purposes
     */
    private Object[] primitive_args;
    private String primitive_key;


    public JPrimitive(int type, String key, IPrimitives lib, Method m, int arity) throws NoSuchMethodException {
        if (m == null) {
            throw new NoSuchMethodException();
        }
        this.type = type;
        primitive_key = key;
        source = lib;
        method = m;
        primitive_args = new Term[arity];
    }

    @Override
    public String getKey() {
        return primitive_key;
    }

    @Override
    public boolean isDirective() {
        return (type == PrimitiveInfo$.MODULE$.DIRECTIVE());
    }

    @Override
    public boolean isFunctor() {
        return (type == PrimitiveInfo$.MODULE$.FUNCTOR());
    }

    @Override
    public boolean isPredicate() {
        return (type == PrimitiveInfo$.MODULE$.PREDICATE());
    }


    @Override
    public int getType() {
        return type;
    }

    @Override
    public IPrimitives getSource() {
        return source;
    }


    /**
     * evaluates the primitive as a directive
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws Exception                 if invocation directive failure
     */
    @Override
    public synchronized void evalAsDirective(Struct g) throws IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < primitive_args.length; i++) {
            primitive_args[i] = g.getTerm(i);
        }
        method.invoke(source, primitive_args);
    }


    /**
     * evaluates the primitive as a predicate
     *
     * @throws Exception if invocation primitive failure
     */
    @Override
    public synchronized boolean evalAsPredicate(Struct g) throws Throwable {
        for (int i = 0; i < primitive_args.length; i++) {
            primitive_args[i] = g.getArg(i);
        }
        try {
            //System.out.println("PRIMITIVE INFO evalAsPredicate sto invocando metodo "+method.getName());
            return (Boolean) method.invoke(source, primitive_args);
        } catch (InvocationTargetException e) {
            // throw new Exception(e.getCause());
            throw e.getCause();
        }
    }


    /**
     * evaluates the primitive as a functor
     *
     * @throws Throwable
     */
    @Override
    public synchronized Term evalAsFunctor(Struct g) throws Throwable {
        try {
            for (int i = 0; i < primitive_args.length; i++) {
                primitive_args[i] = g.getTerm(i);
            }
            return ((Term) method.invoke(source, primitive_args));
        } catch (Exception ex) {
            throw ex.getCause();
        }
    }


    @Override
    public String toString() {
        return "[ primitive: method " + method.getName() + " - " + primitive_args + " - N args: " + primitive_args.length + " - " + source.getClass().getName() + " ]\n";
    }

}