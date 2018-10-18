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
package com.szadowsz.gospel.core.data.matchers

import com.szadowsz.gospel.core.data.{Struct, Term}

private[core] object Atom {

  /**
    * Determines whether `t` is a fatal exception.
    *
    * @return true when `t` is '''not''' a fatal exception.
    */
  def apply(t: Term): Boolean = t.isAtom

  /**
    * A deconstructor to be used in pattern matches, allowing use in exception
    * handlers.
    *
    * {{{
    * try dangerousOperation() catch {
    *   case NonFatal(e) => log.error("Chillax")
    *   case e => log.error("Freak out")
    * }
    * }}}
    */
  def unapply(t: Term): Option[Struct] = Some(t).filter(apply).map(_.getBinding.asInstanceOf[Struct])

}
