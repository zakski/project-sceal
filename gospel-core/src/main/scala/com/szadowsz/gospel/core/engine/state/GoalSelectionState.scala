/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *//*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.szadowsz.gospel.core.engine.state

import com.szadowsz.gospel.core.data.{Struct, Term}
import com.szadowsz.gospel.core.engine.{Engine, EngineRunner}

/**
  * @author Alex Benini
  *
  */
private[engine] final case class GoalSelectionState(override protected val runner: EngineRunner) extends State {
  protected override val stateName = "Call"

  override def doJob(e: Engine): Unit = {
    var curGoal: Term = null
    while (curGoal == null) {
      curGoal = e.currentContext.goalsToEval.fetch().orNull
      if (curGoal == null) {
        // demo termination
        if (e.currentContext.fatherCtx == null) {
          //verify ChoicePoint
          e.nextState = if (e.choicePointSelector.existChoicePoint) runner.END_TRUE_CP else runner.END_TRUE
          return
        }
        // Caso di rimozione di un contesto di esecuzione
        e.currentContext = e.currentContext.fatherCtx
      } else {
        // Caso di individuazione curGoal
        val goal_app: Term = curGoal.getTerm
        if (!goal_app.isInstanceOf[Struct]) {
          e.nextState = runner.END_FALSE
          return
        }
        // Code inserted to allow evaluation of meta-clause
        // such as p(X) :- X. When evaluating directly terms,
        // they are converted to execution of a call/1 predicate.
        // This enables the dynamic linking of built-ins for
        // terms coming from outside the demonstration context.
        if (curGoal ne goal_app) curGoal = new Struct("call", goal_app)
        e.currentContext.currentGoal = curGoal.asInstanceOf[Struct]
        e.nextState = runner.GOAL_EVALUATION
        return
      }
    }
  }
}