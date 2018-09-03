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

package com.github.alexandrepiveteau.distributed.cvrdts

/**
 * A class representing a [Collection] of elements that are a [GSet]. The particularity of this
 * collection is the fact that it can not see its internal number of elements being reduced; it will
 * always be growing.
 *
 * @param elements The [Set] of the elements that are contained in this [GSet].
 *
 * @param T The type of the elements that are present in this [GSet].
 *
 * @author Alexandre Piveteau
 */
class GSet<T> internal constructor(private val elements: Set<T>) : Set<T> {

    /**
     * Instantiates a new [GSet] that is empty.
     */
    constructor() : this(emptySet())

    override val size = elements.size

    override fun contains(element: T)  = elements.contains(element)

    override fun containsAll(elements: Collection<T>) = this.elements.containsAll(elements)

    override fun isEmpty() = elements.isEmpty()

    override fun iterator() = elements.iterator()

    operator fun plus(element: T): GSet<T> = GSet(elements + element)

    operator fun plus(other: Collection<T>): GSet<T> = GSet(elements + other)

    /**
     * Merges a [GSet] instance with another [GSet] instance. This operation is commutative,
     * since elements of the [GSet] that are equals will be merged.
     *
     * @param other Th [GSet] instance on which the operation of merging should be applied.
     *
     * @return A new [GSet] instance, containing the merged content.
     */
    fun merge(other: GSet<T>): GSet<T> = GSet(elements + other.elements)
}