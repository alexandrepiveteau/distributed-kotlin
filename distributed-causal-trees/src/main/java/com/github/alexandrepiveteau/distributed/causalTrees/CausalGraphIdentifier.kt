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
 * A class representing a [CausalGraphIdentifier], that will be in charge of uniquely identifying
 * an instance of a [CausalGraphAtom]. Each identifier will act as a [Pair] of the site [S] that
 * the [CausalGraphAtom] was issued by, as well as an [Int] indicating the "index" of issuance for
 * the operation at this specific site.
 *
 * Using the [index], a total order of operations can be created on a per-site basis. This order
 * does not reflect any sort of causality between operations - it just provides some information
 * relevant to the source of operations.
 *
 * @param S The type of the sites that will be managing the Yarns of this [CausalGraph].
 *
 * @param site The site that will have issued the atom linked to this [CausalGraphIdentifier].
 * @param index The index of the operation referenced by this [CausalGraphIdentifier].
 */
data class CausalGraphIdentifier<S>(val site: S, val index: Int)