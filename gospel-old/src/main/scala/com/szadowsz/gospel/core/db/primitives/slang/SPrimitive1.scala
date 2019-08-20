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
 */
package com.szadowsz.gospel.core.db.primitives.slang

import alice.tuprolog.IPrimitives
import com.szadowsz.gospel.core.data.{Struct, Term}
import com.szadowsz.gospel.core.db.primitives.SPrimitive

private[primitives] final class SPrimitive1(pType : Int, pKey: String, source: IPrimitives, val func: AnyRef) extends SPrimitive(pType, pKey, source,1) {

  override def evalAsDirective(g: Struct): Unit = {
   func.asInstanceOf[Function1[Term,Unit]](g.getTerm(0))
  }

  override def evalAsPredicate(g: Struct): Boolean = {
    func.asInstanceOf[Function1[Term,Boolean]](g.getTerm(0))
  }

  override def evalAsFunctor(g: Struct): Term = {
    func.asInstanceOf[Function1[Term,Term]](g.getTerm(0))
  }
}