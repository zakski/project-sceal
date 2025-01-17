package com.szadowsz.gospel.core.engine.state

import java.util

import com.szadowsz.gospel.core.data.{Struct, Term, Var}
import com.szadowsz.gospel.core.engine.context.subgoal.tree.SubGoalTree
import com.szadowsz.gospel.core.engine.{Engine, EngineRunner}

import scala.collection.JavaConverters._

/**
  * @author Matteo Iuliani
  */
private[engine] final case class ExceptionState(override protected val runner: EngineRunner) extends State {
  protected override val stateName = "Exception"

  private val catchTerm: Term = runner.getWam.createTerm("catch(Goal, Catcher, Handler)")
  private val javaCatchTerm: Term = runner.getWam.createTerm("java_catch(Goal, List, Finally)")

  override def doJob(e: Engine): Unit = {
    val errorType: String = e.currentContext.currentGoal.getName
    if (errorType == "throw") prologError(e) else javaException(e)
  }

  private def prologError(e: Engine): Unit = {
    val errorTerm: Term = e.currentContext.currentGoal.getArg(0)
    e.currentContext = e.currentContext.fatherCtx

    // step to the halt state if the error cannot be handled
    if (e.currentContext == null) {
      e.nextState = runner.END_HALT
      return
    }

    while (true) {
      // backward tree search for a resolution of Subgoal catch/3 whose second argument unifies with the Exception thrown
      // we have identified the ExecutionContext with the proper subgoal Catch/3
      if (e.currentContext.currentGoal.`match`(catchTerm) && e.currentContext.currentGoal.getArg(1).`match`(errorTerm)) {
        runner.cut() // Cut all choice points generated by the Erroneous Goal

        // Unify the argument of throw / 1 with the second argument of Catch / 3
        val unifiedVars: util.List[Var] = e.currentContext.trailingVars.getHead
        e.currentContext.currentGoal.getArg(1).unify(unifiedVars, unifiedVars, errorTerm, runner.getWam.getFlagManager.isOccursCheckEnabled)


        // insert the manager of the error to the head of the Subgoal list
        // to perform the third argument of catch/3. The manager must
        // also be prepared for maintaining the replacements during the process of
        // unification between the argument of throw/1 and the second argument of catch/3
        var handlerTerm: Term = e.currentContext.currentGoal.getArg(2)
        val curHandlerTerm: Term = handlerTerm.getTerm

        // step to the halt state if the error cannot be handled
        if (!curHandlerTerm.isInstanceOf[Struct]) {
          e.nextState = runner.END_FALSE
          return
        }

        // Code inserted to allow evaluation of meta-clause
        // such as p(X) :- X. When evaluating directly terms,
        // they are converted to execution of a call/1 predicate.
        // This enables the dynamic linking of built-ins for
        // terms coming from outside the demonstration context.
        if (handlerTerm ne curHandlerTerm)
          handlerTerm = new Struct("call", curHandlerTerm)

        val handler: Struct = handlerTerm.asInstanceOf[Struct]
        runner.identify(handler)
        val sgt: SubGoalTree = new SubGoalTree
        sgt.addLeaf(handler)
        runner.pushSubGoal(sgt)
        e.currentContext.currentGoal = handler
        e.nextState = runner.GOAL_SELECTION
        return

      } else {
        // step to the halt state if the error cannot be handled
        e.currentContext = e.currentContext.fatherCtx
        if (e.currentContext == null) {
          e.nextState = runner.END_HALT
          return
        }
      }
    }
  }

  private def javaException(e: Engine): Unit = {
    val exceptionTerm: Term = e.currentContext.currentGoal.getArg(0)
    e.currentContext = e.currentContext.fatherCtx

    // step to the halt state if the error cannot be handled
    if (e.currentContext == null) {
      e.nextState = runner.END_HALT
      return
    }

    while (true) {
      // backward tree search for a resolution of Subgoal java_catch/3 whose argument unifies with the Exception thrown

      // we have identified the ExecutionContext with the proper subgoal java_Catch/3
      if (e.currentContext.currentGoal.`match`(javaCatchTerm) && javaMatch(e.currentContext.currentGoal.getArg(1), exceptionTerm)) {
        runner.cut() // cut all the choice points generated by JavaGoal

        // Unify the topic of java_throw/1 with the appropriate catch
        val unifiedVars: util.List[Var] = e.currentContext.trailingVars.getHead
        var handlerTerm: Term = javaUnify(e.currentContext.currentGoal.getArg(1), exceptionTerm, unifiedVars)
        if (handlerTerm == null) {
          e.nextState = runner.END_FALSE
          return
        }

        // Insert the catch and (if present) finally blocks at the head of
        //  the subgoals to perform List. The two predicates must also
        // be prepared for implementing & maintaining the substitutions
        // Made during the process of unification between
        // The exception and the catch block
        val curHandlerTerm: Term = handlerTerm.getTerm
        if (!curHandlerTerm.isInstanceOf[Struct]) {
          e.nextState = runner.END_FALSE
          return
        }
        var finallyTerm: Term = e.currentContext.currentGoal.getArg(2)
        val curFinallyTerm: Term = finallyTerm.getTerm

        // check if we have a finally block
        var isFinally: Boolean = true
        if (curFinallyTerm.isInstanceOf[Int]) {
          val finallyInt: Int = curFinallyTerm.asInstanceOf[Int]
          if (finallyInt.intValue == 0) {
            isFinally = false
          } else {
            // syntax error
            e.nextState = runner.END_FALSE
            return
          }
        } else if (!curFinallyTerm.isInstanceOf[Struct]) {
          e.nextState = runner.END_FALSE
          return
        }

        // Code inserted to allow evaluation of meta-clause
        // such as p(X) :- X. When evaluating directly terms,
        // they are converted to execution of a call/1 predicate.
        // This enables the dynamic linking of built-ins for
        // terms coming from outside the demonstration context.
        if (handlerTerm ne curHandlerTerm)
          handlerTerm = new Struct("call", curHandlerTerm)
        if (finallyTerm ne curFinallyTerm)
          finallyTerm = new Struct("call", curFinallyTerm)

        val handler: Struct = handlerTerm.asInstanceOf[Struct]
        runner.identify(handler)
        val sgt: SubGoalTree = new SubGoalTree
        sgt.addLeaf(handler)
        if (isFinally) {
          val finallyStruct: Struct = finallyTerm.asInstanceOf[Struct]
          runner.identify(finallyStruct)
          sgt.addLeaf(finallyStruct)
        }
        runner.pushSubGoal(sgt)
        e.currentContext.currentGoal = handler
        e.nextState = runner.GOAL_SELECTION
        return
      }
      else {
        e.currentContext = e.currentContext.fatherCtx
        if (e.currentContext == null) {
          e.nextState = runner.END_HALT
          return
        }
      }
    }
  }

  // checks whether the term is a catch mergeable with the argument of the exception thrown
  private def javaMatch(arg1: Term, exceptionTerm: Term): Boolean = {
    if (!arg1.isList) return false
    val list: Struct = arg1.asInstanceOf[Struct]
    if (list.isEmptyList) return false
    val it: util.Iterator[_ <: Term] = list.listIterator
    for (nextTerm <- it.asScala) {
      if (nextTerm.isCompound) {
        val element: Struct = nextTerm.asInstanceOf[Struct]

        if ((element.getName == ",") && element.getArity == 2) {
          if (element.getArg(0).`match`(exceptionTerm)) {
            return true
          }
        }
      }
    }
    false
  }

  //  Unifies the predicate of java_throw/1 with the right catch statement and returns the corresponding handler
  private def javaUnify(arg1: Term, exceptionTerm: Term, unifiedVars: util.List[Var]): Term = {
    val list: Struct = arg1.asInstanceOf[Struct]
    val it: util.Iterator[_ <: Term] = list.listIterator
    for (nextTerm <- it.asScala) {
      val element: Struct = nextTerm.asInstanceOf[Struct]
      if ((element.getName == ",") && element.getArity == 2) {
        if (element.getArg(0).`match`(exceptionTerm)) {
          element.getArg(0).unify(unifiedVars, unifiedVars, exceptionTerm, runner.getWam.getFlagManager.isOccursCheckEnabled)
          return element.getArg(1)
        }
      }
    }
    null
  }
}