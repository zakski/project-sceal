/*
 * JavaTerm.java
 *
 * Created on May 7, 2007, 11:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.model;

import alice.tuprologx.pj.annotations.Termifiable;
import com.szadowsz.gospel.core.data.Struct;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author maurizio
 */
public class JavaTerm<O> extends Compound<JavaTerm<O>> {

    public static final HashMap<String, Class<?>> hashtable = new HashMap<>();
    private final Class<?> _class;
    private final java.util.Collection<Term<?>> _properties;
    public JavaTerm(O o) {
        this(o.getClass(), getProperties(o));
    }

    private JavaTerm(Class<?> _class, java.util.Collection<Term<?>> properties) {
        this._class = _class;
        _properties = properties;
    }

    /**
     * Creates a new instance of JavaTerm
     */
    /*private static List<Compound2<Atom,Term<?>>> getTermList(Object po) {
        try {
            java.util.Vector<Compound2<Atom,Term<?>>> termArr = new java.util.Vector<Compound2<Atom,Term<?>>>();
            java.beans.BeanInfo binfo = java.beans.Introspector.getBeanInfo(po.getClass());
            for (java.beans.PropertyDescriptor pdesc : binfo.getPropertyDescriptors()) {
                //only read-write properties are translated into a compound
                if (pdesc.getReadMethod()!=null && pdesc.getWriteMethod()!=null) {
                    Object o = pdesc.getReadMethod().invoke(po);
                    Atom propertyName = new Atom(pdesc.getName());
                    Term<?> propertyValue = (o != null) ? Term.fromJava(o) : new Var(((String)propertyName.toJava()).toUpperCase());
                    termArr.add(new Compound2<Atom,Term<?>>("_property",propertyName, propertyValue));
                }
            }
            System.out.println(termArr);
            return new List<Compound2<Atom,Term<?>>>(termArr);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        }
    }*/
    private static Vector<Term<?>> getProperties(Object _object) {
        Vector<Term<?>> termArr;
        try {
            termArr = new java.util.Vector<>();
            java.beans.BeanInfo binfo = java.beans.Introspector.getBeanInfo(_object.getClass());
            int count = 0;
            for (java.beans.PropertyDescriptor pdesc : binfo.getPropertyDescriptors()) {
                //only read-write properties are translated into a compound
                if (pdesc.getReadMethod() != null && pdesc.getWriteMethod() != null) {
                    Object o = pdesc.getReadMethod().invoke(_object);
                    Term<?> t = o != null ? Term.fromJava(o) : new Var<>("X" + count);
                    termArr.add(t);
                    count++;
                }
            }
            //System.out.println(termArr);

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        }
        if (termArr == null || termArr.size() == 0) {
            throw new IllegalArgumentException();

        }
        return termArr;
    }

    static boolean matches(com.szadowsz.gospel.core.data.Term t) {
//        try {
//            return (!(t instanceof com.szadowsz.gospel.core.data.Var) && t.isCompound() && !t.isList() && Class.forName(((com.szadowsz.gospel.core.data.Struct)t).getName())!=null);
//        }
//        catch (Exception e) {
//            return false;
//        }
        return (t instanceof TermifiableStruct<?>) || ((t.getTerm() instanceof Struct) && hashtable.containsKey(((Struct) t.getTerm()).getName()));
    }

    static <Z> JavaTerm<Z> unmarshalObject(Struct s) {
        if (!matches(s))
            throw new UnsupportedOperationException();
        Class<?> termKlass = hashtable.get(s.getName());
        Vector<Term<?>> terms = new Vector<>();
        for (int i = 0; i < s.getArity(); i++) {
            terms.add(Term.unmarshal(s.getArg(i)));
        }
        return new JavaTerm<>(termKlass, terms);
    }
    /*
    private static String getName(Object po) {
        try {
            java.util.Vector<Compound2<Atom,Term<?>>> termArr = new java.util.Vector<Compound2<Atom,Term<?>>>();
            java.beans.BeanInfo binfo = java.beans.Introspector.getBeanInfo(po.getClass());
            return binfo.getBeanDescriptor().getBeanClass().getName();
        }        
        catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        }
    }
    */

    /**
     * Creates a new instance of JavaTerm
     */
    public Struct marshal() {
        try {

            com.szadowsz.gospel.core.data.Term[] termArr = new com.szadowsz.gospel.core.data.Term[_properties.size()];
            int count = 0;
            for (Term<?> term : _properties) {
                //only read-write properties are translated into a compound
                termArr[count] = term.marshal();
                count++;
            }
            //System.out.println(java.util.Arrays.asList(termArr));
            hashtable.put(getName(), this._class);
            return new TermifiableStruct<O>(getName(), termArr).setJavaTerm(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        }
    }

    protected String getName() {
        return _class.getAnnotation(Termifiable.class).predicate();
    }

    public Class<?> getKlass() {
        return _class;
    }

    public int arity() {
        return _properties.size();
    }

    public <Z> Z toJava() {
        try {
            Object po = _class.newInstance();
            java.beans.BeanInfo binfo = java.beans.Introspector.getBeanInfo(_class);
            //int i = 0;
            java.util.Iterator<Term<?>> it = _properties.iterator();
            for (java.beans.PropertyDescriptor pdesc : binfo.getPropertyDescriptors()) {
                if (pdesc.getReadMethod() != null && pdesc.getWriteMethod() != null) {
                    Term<?> property = it.next();
                    /* ED 2013-05-21 */
                    Var<Term<?>> auxProperty = uncheckedCast(property);
                    //if (!((property instanceof Var) && ((Var<Term<?>>)property).getValue()==null)) {
                    if (!((property instanceof Var) && (auxProperty).getValue() == null)) {
                        //System.out.println(property.toJava().getClass() + " " + pdesc.getWriteMethod().getName());
                        pdesc.getWriteMethod().invoke(po, property.toJava());
                    }
                }
            }
            // return (Z)po;
            return uncheckedCast(po);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String toString() {
        return getName() + _properties;
    }

    @SuppressWarnings("serial")
    static class TermifiableStruct<O> extends Struct {
        JavaTerm<O> _term;

        TermifiableStruct(String name, com.szadowsz.gospel.core.data.Term[] arr) {
            super(name, arr);
        }

        JavaTerm<O> getJavaTerm() {
            return _term;
        }

        TermifiableStruct<O> setJavaTerm(JavaTerm<O> term) {
            _term = term;
            return this;
        }
    }
}
