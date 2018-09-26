/*
 * MIT License
 *
 * Copyright (c) 2018 Alexandre Piveteau
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.alexandrepiveteau.distributed.causalTrees

import com.github.alexandrepiveteau.functional.monads.Either

/**
 * A data class representing the different functions that will be available in a [CausalTreeAtom].
 * The responsibility of a [CausalTreeAtom] is to keep a reference to a single instance, as well as
 * the causality relation between different elements of the data structure.
 *
 * @param cause An [Either] monad, indicating whether the cause of this particular atom is the root
 *              or a posterior atom.
 * @param value The error that will be contained in this atom. This should be an atomic, commutative
 *              operation.
 *
 * @param E The type of the element that will be contained within this atom.
 * @param S The type of the site identifiers that will be associated with each atom.
 *
 * @author Alexandre Piveteau
 */
data class CausalTreeAtom<out E, S> (
        val cause: Either<CausalTreeYarn.Root, CausalTreeAtom.Identifier<S>>,
        val identifier: Identifier<S>,
        val value: E) {

    // Site : Site corresponding to the atom.
    // Index : Position of the atom in its site yarn.
    // Timestamp : Lamport timestamp for the associated atom.
    data class Identifier<out S>(val site: S, val index: Int, val timestamp: Int)
}