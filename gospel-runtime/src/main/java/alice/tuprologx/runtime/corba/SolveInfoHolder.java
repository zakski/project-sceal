package alice.tuprologx.runtime.corba;

/**
 * org/alice/tuprologx/runtime/corba/SolveInfoHolder.java
 * Generated by the IDL-to-Java compiler (portable), version "3.0"
 * from org/alice/tuprologx/runtime/corba/Prolog.idl
 * venerd? 28 dicembre 2001 12.37.09 GMT+01:00
 */

public final class SolveInfoHolder implements org.omg.CORBA.portable.Streamable {
    public alice.tuprologx.runtime.corba.SolveInfo value = null;

    public SolveInfoHolder() {
    }

    public SolveInfoHolder(alice.tuprologx.runtime.corba.SolveInfo initialValue) {
        value = initialValue;
    }

    public void _read(org.omg.CORBA.portable.InputStream i) {
        value = alice.tuprologx.runtime.corba.SolveInfoHelper.read(i);
    }

    public void _write(org.omg.CORBA.portable.OutputStream o) {
        alice.tuprologx.runtime.corba.SolveInfoHelper.write(o, value);
    }

    public org.omg.CORBA.TypeCode _type() {
        return alice.tuprologx.runtime.corba.SolveInfoHelper.type();
    }

}