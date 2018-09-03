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

/**
 * An element of a [WootSequence] that will be used to represent an atomic, non-splittable block
 * of the sequence. Typically, this could be a single character in a text, or some sort of
 * logical block in a more complex linear structure like a tree.
 *
 * @param identifier The [WootIdentifier] that will be assigned to this specific block.
 * @param value The value of this particular [WootElement], that will be domain-specific.
 * @param visible A [Boolean] value, indicating whether this [WootElement] is present in the
 *                sequence.
 * @param previousValueIdentifier The [WootIdentifier] of the previous element of the structure.
 * @param nextValueIdentifier The [WootIdentifier] of the next element of the structure.
 *
 * @author Alexandre Piveteau
 */
data class WootElement<S: Comparable<S>, out T>(
        val identifier: WootIdentifier<S>,
        val value: Maybe<T>,
        var visible: Boolean,
        val previousValueIdentifier: WootIdentifier<S>,
        val nextValueIdentifier: WootIdentifier<S>)