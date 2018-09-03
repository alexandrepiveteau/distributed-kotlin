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
 * A class representing a [Collection] of elements that are a [PNSet]. The particularity of this
 * collection is the fact that it will support concurrent modifications and will always end in a
 * consistent states, since it's just the cross product of two [GSet]s.
 *
 * @param positive The [Set] of the elements that were added to this [PNSet].
 * @param negative The [Set] of the elements that were removed from this [PNSet].
 *
 * @param T The type of the elements that are present in this [GSet].
 *
 * @author Alexandre Piveteau
 */
class PNSet<T> internal constructor(private val positive: Set<T>, private val negative: Set<T>) : Collection<T> {

    /**
     * Instantiates a new [PNSet] that is empty.
     */
    constructor() : this(emptySet(), emptySet())

    override val size = positive.size - (positive.union(negative).size)

    override fun contains(element: T) = positive.contains(element) && negative.contains(element).not()

    override fun containsAll(elements: Collection<T>) = positive.containsAll(elements) && negative.containsAll(elements).not()

    override fun isEmpty() = positive.union(negative) == positive

    override fun iterator() = (positive - (positive.union(negative))).iterator()

    operator fun plus(element: T): PNSet<T> = PNSet(positive + element, negative)

    operator fun plus(other: Collection<T>): PNSet<T> = PNSet(positive + other, negative)

    operator fun minus(element: T): PNSet<T> = PNSet(positive, negative + element)

    operator fun minus(other: Collection<T>): PNSet<T> = PNSet(positive, negative + other)

    /**
     * Merges a [PNSet] with another [PNSet] instance. This operation is commutative, since elements
     * of the [PNSet] that are equal will be merged.
     *
     * @param other The [PNSet] instance on which the operation of merging should be applied.
     *
     * @return A new [PNSet] instance, containing the merged content.
     */
    fun merge(other: PNSet<T>): PNSet<T> = PNSet(positive + other.positive, negative + other.negative)
}