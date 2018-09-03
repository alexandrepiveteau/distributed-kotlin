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

package com.github.alexandrepiveteau.distributed.woot

import com.github.alexandrepiveteau.functional.monads.Maybe
import com.github.alexandrepiveteau.functional.monads.emptyMaybe
import com.github.alexandrepiveteau.functional.monads.maybeOf

/**
 * A class representing a [Collection] of elements that are a [WootSequence]. A [WootSequence] is a
 * CRDT sequence that works without Operation Transform. It supports commutative operations across
 * multiple independent peers.
 *
 * This data structure will be mutable, in order to provide some better performance on devices with
 * a high overhead for garbage collection.
 *
 * TODO : Change the [WootIdentifier] such that it supports other site-specific clock types.
 *
 * @param localIdentifier The identifier of the [S]ite where this [WootSequence] is stored.
 * @param localClock The [Long] representing the current clock at the local site.
 *
 * @param S The type of the site identifiers that will be used. Must be [Comparable].
 * @param T The type of the elements that are present in this [WootSequence].
 *
 * @author Alexandre Piveteau
 */
class WootSequence<S: Comparable<S>, T>(
        val localIdentifier: S,
        private var localClock: Long = 0) {

    /**
     * The local, site-specific [List] of [WootElement]s that will be used to define this
     * [WootSequence]. When the [WootSequence] has just been created, this [List] will be empty and
     * contain only the extremities.
     */
    private val localElements: MutableList<WootElement<S, T>> = mutableListOf(
            WootElement(WootIdentifier.Start(), emptyMaybe(), false, WootIdentifier.Start(), WootIdentifier.End()),
            WootElement(WootIdentifier.End(), emptyMaybe(), false, WootIdentifier.Start(), WootIdentifier.End())
    )

    /**
     * The local, site-specific operations that are pending. They are preserved in a pool until they
     * will have been applied and integrated to the [localElements].
     */
    private val localPendingOperations: MutableList<WootOperation<S, T>> = mutableListOf()

    /**
     * A [MutableSet] of all the [WootOperation]s that will have been already integrated into this
     * instance of [WootSequence]. This will also be used to ensure that the operations are not
     * played multiple times.
     */
    private val localIntegratedOperations: MutableSet<WootOperation<S, T>> = mutableSetOf()

    /**
     * Applies the pending [WootOperation]s that should be applied to this particular instance of a
     * [WootSequence]. The operations that will have to be applied will be performed in an order
     * that preserves the intention semantics of the user of the [WootSequence].
     *
     * If the local pending operations still require some additional operations before being
     * performed, they will not be applied until the said operations have been successfully
     * dispatched to this [WootSequence] through the [enqueueOperation] method.
     */
    fun applyPendingOperations() {
        while (localPendingOperations.isNotEmpty()) {
            if (checkOperationExecutable(localPendingOperations.first()).not()) {
                localPendingOperations.sortByDescending { checkOperationExecutable(it) }
            }
            if (checkOperationExecutable(localPendingOperations.first()).not()) {
                return // Ensures that we can apply the first operation.
            }
            val operation = localPendingOperations.removeAt(0)
            if (localIntegratedOperations.contains(operation)) {
                break // Ensures that we do not apply an operation multiple times.
            }
            val element = operation.element
            if (operation is WootOperation.DeleteOperation) {
                integrateDeletion(element)
            } else if (operation is WootOperation.InsertOperation) {
                integrateInsertion(element, element.previousValueIdentifier, element.nextValueIdentifier)
            }
        }
    }

    /**
     * Checks whether a certain [WootOperation] is executable with the current state of the
     * [WootSequence] and returns a [Boolean] indicating whether it's the case or not. In fact, when
     * some missing operations have not been delivered yet, the [WootOperation]s might not be
     * applicable.
     *
     * @param operation The [WootOperation] for which we would like to know if it is executable.
     *
     * @return A [Boolean] value indicating if the operation is indeed executable or not.
     */
    private fun checkOperationExecutable(operation: WootOperation<S, T>): Boolean {
        val element = operation.element
        return when (operation) {
            is WootOperation.DeleteOperation -> {
                localElements.contains(element)
            }
            is WootOperation.InsertOperation -> {
                val localElementsIdentifiers = localElements.map { it.identifier }
                localElementsIdentifiers.contains(element.nextValueIdentifier) &&
                        localElementsIdentifiers.contains(element.previousValueIdentifier)
            }
        }
    }

    /**
     * Enqueues a [WootOperation] that will have to be applied to this [WootSequence] at some point
     * in the future. In particular, the [WootOperation] will be applied only once the dedicated
     * method [applyPendingOperations] will have been called.
     *
     * @param operation The [WootOperation] that will have to be applied on this [WootSequence].
     *
     * @see applyPendingOperations
     */
    fun enqueueOperation(operation: WootOperation<S, T>) {
        // Check that this operation was not already applied anyhow. Identifier already there ?
        if (localElements.contains(operation.element).not() && localPendingOperations.contains(operation).not()) {
            localPendingOperations += operation
        }
    }

    /**
     * Deletes the element visible at the i-th index of the current [WootSequence] instance. Once an
     * element has been deleted from a [WootSequence] by a site, it will have to be re-added in
     * order to be visible again to the user.
     *
     * This method does not perform the operation, but generates a [WootOperation] that can then
     * be distributed and applied to each [WootSequence] independently.
     *
     * @param index The [Int] index that corresponds to the i-th visible element, that will be
     *              deleted.
     *
     * @return The [WootOperation] that will have to be applied through the [enqueueOperation]
     *         method.
     */
    fun generateDeleteAt(index: Int): WootOperation<S, T> {
        val element = visibleElementAt(index) ?: throw UnsupportedOperationException("No element at index $index.")
        return WootOperation.DeleteOperation(deletedElement = element).apply { integrateDeletion(element) }
    }

    /**
     * Inserts an element at the i-th index of the visible items of the current [WootSequence].
     *
     * This method does not perform the operation, but generates a [WootOperation] that can then
     * be distributed and applied to each [WootSequence] independently.
     *
     * @param index The [Int] index that corresponds to the i-th visible position, that will be
     *              deleted.
     * @param value The element of type [T] that should be inserted at the position [index].
     *
     * @return The [WootOperation] that will have to be applied through the [enqueueOperation]
     *         method.
     */
    fun generateInsertAt(index: Int, value: T): WootOperation<S, T> {
        localClock++
        val previousVisibleIdentifier = visibleElementAt(index)?.identifier ?: WootIdentifier.Start()
        val nextVisibleIdentifier = visibleElementAt(index + 1)?.identifier ?: WootIdentifier.End()
        val elementIdentifier = WootIdentifier.Element(localIdentifier, localClock)
        val element = WootElement(elementIdentifier, maybeOf(value), true, previousVisibleIdentifier, nextVisibleIdentifier)
        return WootOperation.InsertOperation(insertedElement = element).apply { integrateInsertion(element, previousVisibleIdentifier, nextVisibleIdentifier) }
    }

    /**
     * Integrates the deletion of a [WootElement] into the current [WootSequence]. This will perform
     * all of the changes that are necessary to be performed once a [WootElement] has been selected
     * and should be deleted by this site.
     *
     * @param element The [WootElement] instance that should be deleted for this site.
     */
    private fun integrateDeletion(element: WootElement<S, T>) {
        element.visible = false
        localIntegratedOperations += WootOperation.DeleteOperation(element)
    }

    /**
     * Integrates the insertion of a [WootElement] into the current [WootSequence]. This will
     * perform all the changes that are necessary to be performed once a [WootElement] has been
     * chosen and should be inserted by this site.
     *
     * TODO : Optimize the recursion to check less elements.
     *
     * @param element The [WootElement] instance that should be added on this site.
     * @param previousIdentifier The [WootIdentifier] of the preceding element.
     * @param nextIdentifier The [WootIdentifier] of the succeeding element.
     */
    private fun integrateInsertion(element: WootElement<S, T>,
                                   previousIdentifier: WootIdentifier<S>,
                                   nextIdentifier: WootIdentifier<S>) {
        val localElementsIdentifiers = localElements.toList().map { it.identifier }
        val previousElementIndex = localElementsIdentifiers.indexOf(previousIdentifier)
        val nextElementIndex = localElementsIdentifiers.indexOf(nextIdentifier)

        if (nextElementIndex - 1 == previousElementIndex) {
            // No space between the elements, so we can directly add the new element.
            localElements.add(nextElementIndex, element)
            localIntegratedOperations += WootOperation.InsertOperation(element)
        } else {
            // There is some free room between the elements, so we need to recursively perform some
            // calculations to insert properly.

            var index = 0
            while (index < localElements.size && localElements[index].identifier < element.identifier) {
                index++
            }

            // Recursive call, as per the Woot algorithm.
            integrateInsertion(element, localElements[index - 1].identifier, localElements[index].identifier)
        }
    }

    /**
     * Returns the user-visible [Set] of all the [WootOperation]s that have already been integrated
     * into this instance of the [WootSequence]. Identical [WootOperation]s should not be replayed,
     * and a local log of the [WootOperation]s is kept to avoid this.
     */
    fun integratedOperations(): Set<WootOperation<S, T>> = localIntegratedOperations.toSet()

    /**
     * Returns the element visible at the index [Int] from the start of the sequence. Similarly to
     * a standard list, this will return the n-th element that is user-facing.
     */
    private fun visibleElementAt(index: Int): WootElement<S, T>? = localElements
            .toList()
            .filter { it.visible }
            .getOrNull(index)

    /**
     * Returns the user-visible value of the current [WootSequence] that can then be interpreted
     * and directly used by the users of the [WootSequence].
     *
     * @return A [List] of elements of type [T].
     */
    fun value(): List<T> = localElements
            .toList()
            .filter { it.visible && it.value is Maybe.Some }
            .map { it.value as Maybe.Some }
            .map { it.just }
}