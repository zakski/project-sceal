/**
  * This library is free software; you can redistribute it and/or
  * modify it under the terms of the GNU Lesser General Public
  * License as published by the Free Software Foundation; either
  * version 3.0 of the License, or (at your option) any later version.
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
package com.szadowsz.gospel.core.engine

import com.szadowsz.gospel.core.data.{Struct, Var}
import com.szadowsz.gospel.core.engine.state.{GoalSelectionState, InitState}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class ExecutorSpec extends FlatSpec with Matchers with BeforeAndAfter {
  
  behavior of "Executor"
  
  it should "initialise correctly for a simple query" in {
    val query = new Struct("test", new Var("A"), new Var("B"))
    val exec = new Executor(query)
    
    exec.startGoal shouldBe null
    exec.currentContext shouldBe null
    exec.goalVars shouldBe empty
    exec.nextState shouldBe a [InitState]
    
    exec.nextState.doJob(exec)
  
    exec.startGoal shouldBe query
    exec.currentContext.id shouldBe 0
    exec.goalVars should have size 2
    exec.nextState shouldBe a [GoalSelectionState]
  
  }
}
