package alice.util.proxyGenerator;
import javax.tools.*;

import cli.System.Reflection.Assembly;

import java.lang.reflect.*;
import java.util.*;

/* This class uses the private static "defineClass0" method found in Proxy to add the class into the ClassLoader: 
 * an exception is raised if the class already exists in that class loader. 
 * Another approach could be to use a new ClassLoader -- see the Dynamic In-memory Compilation article for an 
 * example of how to do that.
 * Compiling syntax errors will be printed to System.err. 
 * You should replace that code with calls to your favorite logging system.
*/ 

public class Generator {
  private static final Method defineClassMethod;
  private static final JavaCompiler jc;

  
  static {
    try {
      defineClassMethod = Proxy.class.getDeclaredMethod( "defineClass0", ClassLoader.class,
    		  											  String.class, byte[].class, int.class, int.class);
      defineClassMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new ExceptionInInitializerError(e);
    }
    
    if(System.getProperty("java.vm.name").equals("IKVM.NET"))
    	ikvm.runtime.Startup.addBootClassPathAssemby(Assembly.Load("IKVM.OpenJDK.Tools"));
    
    jc = ToolProvider.getSystemJavaCompiler();
    if (jc == null) {
    	throw new UnsupportedOperationException( "Cannot find java compiler!  " + "Probably only JRE installed.");
    }
  }

  public static Class<?> make(ClassLoader loader, String className, CharSequence javaSource) {
	  
    GeneratedClassFile gcf = new GeneratedClassFile();
    DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector<JavaFileObject>();

    boolean result = compile(className, javaSource, gcf, dc);
    return processResults(loader, javaSource, gcf, dc, result);
  }

  
  private static boolean compile( String className, CharSequence javaSource, GeneratedClassFile gcf, 
		  							DiagnosticCollector<JavaFileObject> dc) {
	GeneratedJavaSourceFile gjsf = new GeneratedJavaSourceFile(className, javaSource);
    GeneratingJavaFileManager fileManager = new GeneratingJavaFileManager(jc.getStandardFileManager(dc, null, null), gcf);
    JavaCompiler.CompilationTask task = jc.getTask(null, fileManager, dc, null, null, Arrays.asList(gjsf));
    return task.call();
  }

  private static Class<?> processResults( ClassLoader loader, CharSequence javaSource, GeneratedClassFile gcf, 
		  									DiagnosticCollector<?> dc, boolean result) {
    if (result) {
      return createClass(loader, gcf);
    } else {
      // use your logging system of choice here
      System.err.println("Compile failed:");
      System.err.println(javaSource);
      for (Diagnostic<?> d : dc.getDiagnostics()) {
        System.err.println(d);
      }
      throw new IllegalArgumentException(
          "Could not create proxy - compile failed");
    }
  }

  
  private static Class<?> createClass(ClassLoader loader, GeneratedClassFile gcf) {
    try {
      byte[] data = gcf.getClassAsBytes();
      return (Class<?>) defineClassMethod.invoke( null, loader, null, data, 0, data.length);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException("Proxy problem", e);
    }
  }
}
