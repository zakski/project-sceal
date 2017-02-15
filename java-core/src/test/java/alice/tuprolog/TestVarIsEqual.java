package alice.tuprolog;

import junit.framework.TestCase;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;



/**
 * @author George S. Cowan
 *
 */
public class TestVarIsEqual extends TestCase {
  
  Prolog core;
  String yes = "yes.\n";
  private SysoutListener sysoutListener = new SysoutListener();
  
  protected void setUp() throws Exception {
    super.setUp();
    core = new Prolog();
    core.addOutputListener(sysoutListener);
  }
  
  private class SysoutListener implements OutputListener {
    public StringBuilder builder = new StringBuilder("");
    
    public void onOutput(OutputEvent ev) {
      builder.append(ev.getMsg());
    }
    public String getAllOutput() {
      return builder.toString();
    }
  }
  
  public void testDifferntVarsCompareEqual() throws MalformedGoalException, InvalidTheoryException {
    // theory is modified code from PTTP 
    String theory = "test :- body_for_head_literal_instrumented(d(X,Y),(not_d(X,U);d(X,Y)),Bod).    "
        + "\n" +    "                                                                 "
        + "\n" +    "body_for_head_literal_instrumented(Head,Wff,Body) :-             "
        + "\n" +    "  nl,print('body_for_head_literal input Head: '),print(Head),    "
        + "\n" +    "  nl,print('                             Wff: '),print(Wff),     "
        + "\n" +    "  false -> true ;                                                "
        + "\n" +    "  Wff = (A ; B) ->                                               "
        + "\n" +    "    nl,print('OR'),                                              "
        + "\n" +    "    body_for_head_literal_instrumented(Head,A,A1),               "
        + "\n" +    "    body_for_head_literal_instrumented(Head,B,B1),               "
        + "\n" +    "    conjoin(A1,B1,Body)                                          "
        + "\n" +    "    , nl, print('body_for_head_literal OR - Body: '),print(Body) "
        + "\n" +    "    ;                                                            "
        + "\n" +    "  Wff == Head ->                                                 "
        + "\n" +    "    Body = true;                                                 "
        + "\n" +    "  negated_literal_instrumented(Wff,Head) ->                      "
        + "\n" +    "    print(' '),                                                  "
        + "\n" +    "    Body = false;                                                "
        + "\n" +    "  %true ->                                                       "
        + "\n" +    "    nl,print('OTHERWISE'),                                       "
        + "\n" +    "    negated_literal_instrumented(Wff,Body).                      "
        + "\n" +    "                                                                 "
        + "\n" +    "negated_literal_instrumented(Lit,NotLit) :-                      "
        + "\n" +    "  nl,print('*** negated_literal in Lit:'),print(Lit),            "
        + "\n" +    "  nl,print('***                 NotLit:'),print(NotLit),                              "
        + "\n" +    "  Lit =.. [F1|L1],                                               "
        + "\n" +    "  negated_functor(F1,F2),                                        "
        + "\n" +    "  (var(NotLit) ->                                                "
        + "\n" +    "    NotLit =.. [F2|L1];                                          "
        + "\n" +    "  %true ->                                                       "
        + "\n" +    "    nl,print('                 Not var:'),print(NotLit),                            "
        + "\n" +    "    NotLit =.. [F2|L2],                                          "
        + "\n" +    "    nl,print('***              Lit array:'),print(L1),           "
        + "\n" +    "    nl,print('***           NotLit array:'),print(L2),           "
        + "\n" +    "    L1 == L2                                                     " // ERROR HAPPENS HERE
        + "\n" +    "    , nl,print('***               SUCCEEDS')                     "
        + "\n" +    "    ).                                                           "
        + "\n" +    "                                                                 "
        + "\n" +    "negated_functor(F,NotF) :-                                       "
        + "\n" +    "  atom_chars(F,L),                                               "
        + "\n" +    "  atom_chars(not_,L1),                                           "
        + "\n" +    "  (list_append(L1,L2,L) ->                                       "
        + "\n" +    "    true;                                                        "
        + "\n" +    "  %true ->                                                       "
        + "\n" +    "    list_append(L1,L,L2)),                                       "
        + "\n" +    "  atom_chars(NotF,L2).                                           "
        + "\n" +    "                                                                 "
        + "\n" +    "conjoin(A,B,C) :-                                                "
        + "\n" +    "  A == true ->                                                   "
        + "\n" +    "    C = B;                                                       "
        + "\n" +    "  B == true ->                                                   "
        + "\n" +    "    C = A;                                                       "
        + "\n" +    "  A == false ->                                                  "
        + "\n" +    "    C = false;                                                   "
        + "\n" +    "  B == false ->                                                  "
        + "\n" +    "    C = false;                                                   "
        + "\n" +    "  %true ->                                                       "
        + "\n" +    "    % nl,print('conjoin A: '),print(A),print(' B: '),print(B),   "
        + "\n" +    "    C = (A , B)                                                  "
        + "\n" +    "    % , nl,print('    out A: '),print(A),print(' B: '),print(B)  "
        + "\n" +    "    % , nl,print('        C: '),print(C)                         "
        + "\n" +    "  .                                                              "
        + "\n" +    "                                                                 "
        + "\n" +    "list_append([X|L1],L2,[X|L3]) :-                                 "
        + "\n" +    "  list_append(L1,L2,L3).                                         "
        + "\n" +    "list_append([],L,L).                                             "
        + "\n" +    "                                                                 "
        ;
    
    core.setTheory(new Theory(theory));
    
    SolveInfo info = core.solve("test. ");
    assertTrue("Test should complete normally",info.isSuccess());
    String expected = ""
      + "\n" +    "body_for_head_literal input Head: d(X_e1,Y_e1)"
      + "\n" +    "                             Wff: ';'(not_d(X_e1,U_e1),d(X_e1,Y_e1))"
      + "\n" +    "OR"
      + "\n" +    "body_for_head_literal input Head: d(X_e25,Y_e25)"
      + "\n" +    "                             Wff: not_d(X_e25,U_e25)"
      + "\n" +    "*** negated_literal in Lit:not_d(X_e25,U_e25)  NotLit:d(X_e25,Y_e25)"
      + "\n" +    "***              Lit array:[X_e122,U_e86]"
      + "\n" +    "***           NotLit array:[X_e122,Y_e122]"
      + "\n" +    "OTHERWISE"
      + "\n" +    "*** negated_literal in Lit:not_d(X_e122,U_e86)  NotLit:NotLit_e136"
      + "\n" +    "body_for_head_literal input Head: d(X_e184,Y_e122)"
      + "\n" +    "                             Wff: d(X_e184,Y_e122)"
      + "\n" +    "Wff == Head"
      + "\n" +    "body_for_head_literal OR - Body: d(X_e249,U_e249)"
      + "\n" +    ""
    ;
    
  assertEquals("Var == should not succeed.", expected, sysoutListener.getAllOutput());
  }

}
