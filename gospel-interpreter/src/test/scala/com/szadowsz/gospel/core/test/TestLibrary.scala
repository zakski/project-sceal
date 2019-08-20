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
package com.szadowsz.gospel.core.test

import com.szadowsz.gospel.core.Interpreter
import com.szadowsz.gospel.core.data.Term
import com.szadowsz.gospel.core.db.libraries.{Library, predicate}
import com.szadowsz.gospel.core.db.theory.Theory

class TestLibrary(wam : Interpreter) extends Library(wam) {
  // scalastyle:off method.name
  
  override def getName: String = "test"

  override def getTheory: Option[Theory] = None
  
  /**
    * Method to provide a primitive that will always fail, forcing a backtrack state
    */
  @predicate(2)
  def fail_always_2: (Term, Term) => Boolean = {
    case _ => false
  }
  
  /**
    * Method to provide a primitive that will always throw an exception, forcing an error state
    */
  @predicate(2)
  def throw_always_2: (Term, Term) => Boolean = {
    case _ => throw new Exception("WILL ALWAYS THROW EXCEPTION FOR TEST PURPOSES")
  }
  
  /**
    * Method to provide a primitive that will always throw an exception, forcing an error state
    */
  @predicate(2)
  def throw_interrupt_always_2: (Term, Term) => Boolean = {
    case _ => throw new InterruptedException("WILL ALWAYS THROW EXCEPTION FOR TEST PURPOSES")
  }
}