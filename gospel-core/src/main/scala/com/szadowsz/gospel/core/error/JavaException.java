package com.szadowsz.gospel.core.error;

import com.szadowsz.gospel.core.data.Int;
import com.szadowsz.gospel.core.data.Struct;
import com.szadowsz.gospel.core.data.Term;

/**
 * @author Matteo Iuliani
 */
public class JavaException extends Throwable {
    private static final long serialVersionUID = 1L;
    // eccezione Java che rappresenta l'argomento di java_throw/1
    private final Throwable e;

    public JavaException(Throwable e) {
        this.e = e;
    }

    public Struct getException() {
        // java_exception
        String java_exception = e.getClass().getName();
        // Cause
        Term causeTerm;
        Throwable cause = e.getCause();
        if (cause != null)
            causeTerm = new Struct(cause.toString());
        else
            causeTerm = new Int(0);
        // Message
        Term messageTerm;
        String message = e.getMessage();
        if (message != null)
            messageTerm = new Struct(message);
        else
            messageTerm = new Int(0);
        // StackTrace
        Struct stackTraceTerm = new Struct();
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements)
            stackTraceTerm.append(new Struct(element.toString()));
        // return
        return new Struct(java_exception, causeTerm, messageTerm,
                stackTraceTerm);
    }

}
