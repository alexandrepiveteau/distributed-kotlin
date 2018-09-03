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
 * An identifier that will be used for the [WootSequence] elements and that will be used in
 * order to uniquely identify them. This will be required in order to create a linear ordering
 * of the operations performed on the sequence.
 *
 * Internally, a site-specific clock is managed using a [Long].
 *
 * TODO : Change the [WootIdentifier] such that it supports other site-specific clock types.
 *
 * @param S The type of the site identifiers that will be used. Must be [Comparable].
 *
 * @author Alexandre Piveteau
 */
sealed class WootIdentifier<S: Comparable<S>>: Comparable<WootIdentifier<S>> {

    /**
     * An identifier for a [WootIdentifier] that is at the end of the sequence, i.e. no
     * user-defined characters can succeed it.
     *
     * @see WootIdentifier
     */
    class End<S: Comparable<S>>: WootIdentifier<S>() {

        override fun compareTo(other: WootIdentifier<S>) = if (other is End) 0 else 1

        override fun equals(other: Any?) = other is End<*>

        override fun toString() = "WootIdentifier.End"
    }

    /**
     * An identifier for a [WootElement] that will be in the middle of the sequence, i.e. not at any
     * of the extremities.
     *
     * @param siteIdentifier The identifier of the [S]ite for this identifier.
     * @param siteClock The [Long] indicating the total ordering of the site operation.
     *
     * @see WootIdentifier
     */
    data class Element<S: Comparable<S>>(val siteIdentifier: S, val siteClock: Long): WootIdentifier<S>() {

        override fun compareTo(other: WootIdentifier<S>) = when (other) {
            is Element -> {
                if (siteIdentifier.compareTo(other.siteIdentifier) != 0) {
                    siteIdentifier.compareTo(other.siteIdentifier)
                } else {
                    siteClock.compareTo(other.siteClock)
                }
            }
            is End -> -1
            is Start -> 1
        }
    }

    /**
     * An identifier for a [WootIdentifier] that is at the start of the sequence, i.e. no
     * user-defined characters can precede it.
     *
     * @see WootIdentifier
     */
    class Start<S: Comparable<S>>: WootIdentifier<S>() {

        override fun compareTo(other: WootIdentifier<S>) = if (other is Start) 0 else -1

        override fun equals(other: Any?) = other is Start<*>

        override fun toString() = "WootIdentifier.Start"
    }
}