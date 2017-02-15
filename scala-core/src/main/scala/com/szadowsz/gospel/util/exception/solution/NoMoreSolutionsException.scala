/*
 * tuProlog - Copyright (C) 2001-2015  aliCE team at deis.unibo.it
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
package com.szadowsz.gospel.util.exception.solution

/**
 * This exception means that alternative solutions were asked for, but there are no more available.
 *
 * @author Zakski : 31/08/2015.
 */
@SerialVersionUID(1L)
class NoMoreSolutionsException extends InvalidSolutionException("No More Solutions Available") {

}