package alice.tuprolog.exceptions;

import alice.tuprolog.Int;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * @author Matteo Iuliani
 */
public class JavaException extends Throwable {
	private static final long serialVersionUID = 1L;
    // eccezione Java che rappresenta l'argomento di java_throw/1
    private Throwable e;

    public JavaException(Throwable e) {
        this.e = e;
    }

    public Struct getException() {
        // java_exception
        String java_exception = e.getClass().getName();
        // Cause
        Term causeTerm = null;
        Throwable cause = e.getCause();
        if (cause != null)
            causeTerm = new Struct(cause.toString());
        else
            causeTerm = new Int(0);
        // Message
        Term messageTerm = null;
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
