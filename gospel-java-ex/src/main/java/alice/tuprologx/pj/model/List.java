/*
 * List.java
 *
 * Created on March 8, 2007, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.model;

import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Var;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author maurizio
 */
public class List<X extends Term<?>> extends Term<List<X>> implements Iterable<X> {
    public final static List<?> NIL = new List<>(new Vector<>());
    //public class List<X extends Term<?>> extends Compound<List<X>> {
    final java.util.Vector<X> _theList;

    private List(Vector<X> lt) {
        _theList = lt;
    }

    public <Z> List(Collection<Z> cz) {
        _theList = new Vector<>(cz.size());
        for (Z z : cz) {
            _theList.add(Term.fromJava(z));
        }
    }

    static <Z extends Term<?>> List<Z> unmarshal(Struct s) {
        if (!matches(s))
            throw new UnsupportedOperationException();
        Iterator<? extends com.szadowsz.gospel.core.data.Term> listIt = s.listIterator();
        Vector<Term<?>> termList = new Vector<>();
        while (listIt.hasNext())
            termList.add(Term.unmarshal(listIt.next()));
        return new List<Z>(termList);
    }

    static boolean matches(com.szadowsz.gospel.core.data.Term t) {
        return (!(t instanceof Var) && t.isList() && t instanceof Struct);
    }

    public static List<Atom> tokenize(java.util.StringTokenizer stok) {
        java.util.Vector<String> tokens = new java.util.Vector<>();
        while (stok.hasMoreTokens()) {
            tokens.add(stok.nextToken());
        }
        return new List<>(tokens);
    }

    public <Z> Z/*Collection<Z>*/ toJava() {
        Vector<Z> _javaList = new Vector<>(_theList.size());
        for (Term<?> t : _theList) {
            // _javaList.add( (Z)t.toJava() );
            Z auxList = uncheckedCast(t.toJava());
            _javaList.add(auxList);
        }
        //return (Z)_javaList;
        return uncheckedCast(_javaList);
    }

    public String toString() {
        return "List" + _theList;
    }

    public X getHead() {
        return _theList.get(0);
    }

    public List<X> getTail() {
        //Vector<X> tail = (Vector<X>)_theList.clone();
        Vector<X> tail = uncheckedCast(_theList.clone());
        tail.remove(0);
        return new List<>(tail);
    }

    public Struct marshal() {
        com.szadowsz.gospel.core.data.Term[] termArray = new com.szadowsz.gospel.core.data.Term[_theList.size()];
        int i = 0;
        for (Term<?> t : _theList) {
            termArray[i++] = t.marshal();
        }
        return new Struct(termArray);
    }

    public Iterator<X> iterator() {
        return _theList.iterator();
    }
}