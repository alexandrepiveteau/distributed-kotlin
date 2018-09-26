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

/**
 * An implementation of a [CausalTreeYarn] that will be completely empty. The benefits of using a
 * completely empty yarn is that it can be used for dedicated methods and tasks that require an
 * immutable and optimized yarn to be delivered.
 *
 *  @param E The type of the elements contained in the atoms of this empty yarn.
 */
class EmptyCausalTreeYarn<E, S>: CausalTreeYarn<E, S> {
    override val size = 0
    override fun contains(element: CausalTreeAtom<E, S>) = false
    override fun containsAll(elements: Collection<CausalTreeAtom<E, S>>) = elements.isNotEmpty()
    override fun isEmpty() = true
    override fun iterator() = object : Iterator<CausalTreeAtom<E, S>> {
        override fun hasNext() = false
        override fun next() = error("There are no elements in this yarn.")
    }
}