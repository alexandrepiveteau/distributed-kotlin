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

/**
 * A sealed class representing the different types of [WootOperation]s that can be applied to an
 * instance of a [WootSequence]. Each of the operations behavior will be defined by the
 * [WootElement] that is encoded in the operation.
 *
 * @param element The [WootElement] of the operation.
 *
 * @param S The type of the site identifiers that will be used. Must be [Comparable].
 * @param T The type of the elements that are present in the [WootSequence].
 *
 * @author Alexandre Piveteau
 */
sealed class WootOperation<S: Comparable<S>, out T>(val element: WootElement<S, T>) {

    /**
     * A delete operation on a [WootSequence]. Use the [WootSequence] dedicated method to generate
     * this [WootOperation].
     *
     * @see WootOperation
     * @see WootSequence
     */
    data class DeleteOperation<S: Comparable<S>, out T>(private val deletedElement: WootElement<S, T>): WootOperation<S, T>(deletedElement)

    /**
     * An insert operation on a [WootSequence]. Use the [WootSequence] dedicated method to generate
     * this [WootOperation].
     *
     * @see WootOperation
     */
    data class InsertOperation<S: Comparable<S>, out T>(private val insertedElement: WootElement<S, T>): WootOperation<S, T>(insertedElement)
}