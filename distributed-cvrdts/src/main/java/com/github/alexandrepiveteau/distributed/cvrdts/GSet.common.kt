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
 * Generates a new empty instance of a [GSet]. This method will be part of the only publicly
 * available constructors for generating some [GSet] instances.
 *
 * @param T The type of the elements of this [GSet].
 *
 * @author Alexandre Piveteau
 */
fun <T> emptyGSet() = GSet<T>()

/**
 * Generates a new instance of a [GSet]. This method will be the only publicly available
 * constructor for generating some [GSet] instances.
 *
 * @param elements The elements that the [GSet] instance should be generated with.
 *
 * @param T The type of the elements in this [GSet].
 *
 * @author Alexandre Piveteau
 */
fun <T> gSetOf(vararg elements: T) = GSet(elements.toSet())