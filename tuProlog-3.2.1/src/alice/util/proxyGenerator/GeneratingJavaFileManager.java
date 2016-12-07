package alice.util.proxyGenerator;
import javax.tools.*;
import java.io.*;

class GeneratingJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	
  private final GeneratedClassFile gcf;

  public GeneratingJavaFileManager(StandardJavaFileManager sjfm, GeneratedClassFile gcf) {
    super(sjfm);
    this.gcf = gcf;
  }

  public JavaFileObject getJavaFileForOutput( Location location, String className, JavaFileObject.Kind kind, 
		                                      FileObject sibling) throws IOException {
    return gcf;
  }
}
  

/*
  This class forces the JavaCompiler to use the GeneratedClassFile's output stream for writing the class
*/
 