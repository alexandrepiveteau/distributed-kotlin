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
 * A class representing a [Collection] of elements that are a [MCSet], alias a MaxChangeSet. The
 * particularity of this collection is the fact that it can not see its internal numbers being
 * permanently removed, but still supports additions and deletions across multiple nodes.
 *
 * In particular, when some conflicting changes are detected across multiple nodes, it will keep
 * the results of the node that had the most changes (until synchronization is performed).
 *
 * @param elements The [Map] of the elements that are contained in this [MCSet].
 *
 * @param T The type of the elements that are present in this [MCSet].
 *
 * @author Alexandre Piveteau
 */
class MCSet<T> constructor(private val elements: Map<T, Int>): Set<T> {

    /**
     * Filters the values of a [Map] that are present, according to the inner [MCSet] semantics.
     */
    private fun <K> Map<K, Int>.filterPresent() = filterValues { modificationsCount ->
        modificationsCount % 2 == 0
    }

    /**
     * Instantiates a new [MCSet] that is empty.
     */
    constructor() : this(emptyMap())

    override val size = elements
            .filterPresent()
            .size

    override fun contains(element: T) = elements
            .filterPresent()
            .contains(element)

    override fun containsAll(elements: Collection<T>) = this.elements
            .filterPresent()
            .keys
            .containsAll(elements)

    override fun isEmpty() = this.elements
            .filterPresent()
            .isEmpty()

    override fun iterator() = this.elements
            .filterPresent()
            .keys.iterator()

    operator fun minus(other: T): MCSet<T> {
        return if (contains(other)) {
            increment(other)
        } else {
            this
        }
    }

    // TODO : operator fun minus(other: Collection<T>): MCSet<T>

    operator fun plus(other: T): MCSet<T> {
        return if (contains(other).not()) {
            increment(other)
        } else {
            this
        }
    }

    // TODO : operator fun plus(other: Collection<T>): MCSet<T>

    /**
     * Performs an increment operation on a particular element of the inner [Map] structure. This
     * indicates that a change operation was performed on a particular element.
     *
     * @param element The element of type [T] that should register a change.
     *
     * @return A [MCSet] instance which reflects the change that occurred.
     */
    private fun increment(element: T): MCSet<T> {
        val mutableMap = elements.toMutableMap()
        mutableMap[element] = 1 + (mutableMap[element] ?: -1)
        return MCSet(mutableMap.toMap())
    }

    /**
     * Merges a [MCSet] instance with another [MCSet] instance. This operation is commutative, since
     * elements of the [MCSet] that are equals will be merged.
     *
     * @param other The [PNSet] instance on which the operation of merging should be applied.
     *
     * @return A new [MCSet] instance, containing the merged content.
     */
    fun merge(other: MCSet<T>): MCSet<T> {
        val mergedElements = elements.keys + other.elements.keys
        val mergedEntries = mergedElements.map { element ->
            element to maxOf(elements[element] ?: 0, other.elements[element] ?: 0)
        }
        return MCSet(mergedEntries.toMap())
    }
}