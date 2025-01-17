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
package com.szadowsz.gospel.core.parser;


import com.szadowsz.gospel.core.data.Term;
import com.szadowsz.gospel.core.error.InvalidTermException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class represents an iterator of terms from Prolog text embedded
 * in a parser. Note that this class resembles more a generator than an
 * iterator type. In fact, both {@link TermIterator#next()} and
 * {@link TermIterator#hasNext()} throws {@link InvalidTermException} if
 * the next term they are trying to return or check for contains a syntax
 * error; this is due to both methods trying to generate the next term
 * instead of just returning it or checking for its existence from a pool
 * of already produced terms.
 */
class TermIterator implements Iterator<Term>, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Parser parser;
    private boolean hasNext;
    private Term next;

    TermIterator(Parser p) {
        parser = p;
        next = parser.nextTerm(true);
        hasNext = (next != null);
    }

    public Term next() {
        if (hasNext) {
            if (next == null) {
                next = parser.nextTerm(true);
                if (next == null)
                    throw new NoSuchElementException();
            }
            hasNext = false;
            Term temp = next;
            next = null;
            return temp;
        } else if (hasNext()) {
            hasNext = false;
            Term temp = next;
            next = null;
            return temp;
        }
        throw new NoSuchElementException();
    }

    /**
     * @throws InvalidTermException if, while the parser checks for the existence of the next term, a syntax error is encountered.
     */
    public boolean hasNext() {
        if (hasNext)
            return hasNext;
        next = parser.nextTerm(true);
        if (next != null)
            hasNext = true;
        return hasNext;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}