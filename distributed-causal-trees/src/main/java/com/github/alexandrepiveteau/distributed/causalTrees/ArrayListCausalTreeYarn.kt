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
 * An implementation of a [MutableCausalTreeYarn] that internally uses an [ArrayList] to manage the
 * growth of the
 */
class ArrayListCausalTreeYarn<E, S>: MutableCausalTreeYarn<E, S> {

    private val yarn = arrayListOf<CausalTreeAtom<E, S>>()

    override val size =
            yarn.size

    override fun contains(element: CausalTreeAtom<E, S>) =
            yarn.contains(element)

    override fun containsAll(elements: Collection<CausalTreeAtom<E, S>>) =
            yarn.containsAll(elements)

    override fun isEmpty() =
            yarn.isEmpty()

    override fun iterator() =
            object : MutableIterator<CausalTreeAtom<E, S>> {

                private val yarnIterator = yarn.iterator()

                override fun hasNext() =
                        yarnIterator.hasNext()

                override fun next() =
                        yarnIterator.next()

                override fun remove() =
                        error("CausalTreeYarns can not have their elements removed.")
            }

    override fun add(element: CausalTreeAtom<E, S>) =
            yarn.add(element)

    override fun addAll(elements: Collection<CausalTreeAtom<E, S>>) =
            yarn.addAll(elements)

    override fun clear()
            = error("CausalTreeYarns can not have their elements removed.")

    override fun remove(element: CausalTreeAtom<E, S>) =
            error("CausalTreeYarns can not have their elements removed.")

    override fun removeAll(elements: Collection<CausalTreeAtom<E, S>>) =
            error("CausalTreeYarns can not have their elements removed.")

    override fun retainAll(elements: Collection<CausalTreeAtom<E, S>>) =
            error("CausalTreeYarns can not have their elements removed.")
}