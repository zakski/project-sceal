package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({	BuiltInTestCase.class,
				DoubleTestCase.class,
				PrologTestCase.class, 
				IntTestCase.class, 
				IOLibraryTestCase.class, 
				DoubleTestCase.class, 
				SolveInfoTestCase.class,
				StateRuleSelectionTestCase.class, 
				StructIteratorTestCase.class, 
				StructTestCase.class, 
				TermIteratorTestCase.class,
				TheoryTestCase.class, 
				TheoryManagerTestCase.class, 
				LibraryTestCase.class, 
				JavaLibraryTestCase.class, 
				ParserTestCase.class,
				SpyEventTestCase.class, 
				VarTestCase.class, 
				JavaDynamicClassLoaderTestCase.class,
				TestVarIsEqual.class,
				ISOIOLibraryTestCase.class,
				ThreadLibraryTestCase.class
})
public class TuPrologTestSuite {}
