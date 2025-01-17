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

import java.util

import com.szadowsz.gospel.core.data.{Struct, Var}
import com.szadowsz.gospel.core.engine.{Engine, EngineRunner, ExecutionResultType}

/**
  * @author Alex Benini
  *
  *         End state of demostration.
  */
final private[engine] case class EndState(override protected val runner: EngineRunner, endState: ExecutionResultType.Value) extends State {
  protected override val stateName: String = "End"

  private var goal: Struct = _

  private var vars: util.List[Var] = _

  def getResultType: ExecutionResultType.Value = endState

  def getResultGoal: Struct = goal

  def getResultVars: util.List[Var] = vars

  override def toString: String = {
    endState match {
      case ExecutionResultType.FALSE => "FALSE"
      case ExecutionResultType.TRUE => "TRUE"
      case ExecutionResultType.TRUE_CP => "TRUE_CP"
      case _ => "HALT"
    }
  }

  def doJob(e: Engine): Unit = {
    vars = new util.ArrayList[Var]
    goal = e.startGoal.copyResult(e.goalVars, vars).asInstanceOf[Struct]
  }
}