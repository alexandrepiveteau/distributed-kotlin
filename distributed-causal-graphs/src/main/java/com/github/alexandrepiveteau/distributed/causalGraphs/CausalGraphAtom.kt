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

package com.github.alexandrepiveteau.distributed.causalGraphs

/**
 * A class representing a [CausalGraphAtom], that will always be contained within an instance of
 * a [CausalGraphYarn]. The responsibility of the atom is to keep track of the [operation] that is
 * in the Yarn, as well as the unique [CausalGraphIdentifier] across the [CausalGraph], and the
 * [Set] of all [dependencies] that this [CausalGraphAtom] has.
 *
 * The dependencies help define the causality relationship across multiple sites, or what an
 * operation refers (for instance, when the operation is a delete token). The dependencies will be
 * referenced only by their [CausalGraphIdentifier]s.
 *
 * @param O The type of the operations that will be contained in this [CausalGraph].
 * @param S The type of the sites that will be managing the Yarns of this [CausalGraph].
 *
 * @param operation The instance of the operation saved in this atom.
 * @param identifier The unique [CausalGraphIdentifier] for this atom and operation.
 * @param dependencies The [Set] of causality dependencies that this atom has in the [CausalGraph].
 */
data class CausalGraphAtom<O, S>(
        val operation: O,
        val identifier: CausalGraphIdentifier<S>,
        val dependencies: Set<CausalGraphIdentifier<S>>)