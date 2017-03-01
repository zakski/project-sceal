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

import alice.tuprolog.ClauseInfo
import alice.tuprolog.Struct
import alice.tuprolog.SubGoalStore
import com.szadowsz.gospel.core.engine.context.ExecutionContext
import com.szadowsz.gospel.core.engine.{Engine, EngineRunner}

/**
  * @author Alex Benini
  *
  *         Initial state of demostration
  */
private[engine] final case class InitState(override protected val runner: EngineRunner) extends State {
  override protected val stateName = "Goal"

  override def doJob(e: Engine): Unit = {
    e.prepareGoal()
    val eCtx: ExecutionContext = new ExecutionContext(0) /* Initialize first executionContext */
    eCtx.goalsToEval = new SubGoalStore
    eCtx.goalsToEval.load(ClauseInfo.extractBody(e.startGoal))
    eCtx.clause = e.query.asInstanceOf[Struct]
    eCtx.depth = 0
    eCtx.fatherCtx = null
    eCtx.haveAlternatives = false
    e.initialize(eCtx) /* Initialize WAM environment */
    e.nextState = runner.GOAL_SELECTION /* Set the future state */
  }
}