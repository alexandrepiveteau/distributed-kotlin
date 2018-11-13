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
 * An interface representing a [CausalGraph] data structure. A [CausalGraph] will represent the
 * causality relationships between operations in a distributed system. It is structured around the
 * principle that each site will write only on its own Yarn, and that each operation written on a
 * Yarn will indicate its causality relationships to other operations.
 *
 * Essentially, a [CausalGraph] is a [Collection] of [CausalGraphYarn]s. These yarns will each be
 * managed by a single site, and it will be each site's responsibility to appropriately write on its
 * own Yarn. A control mechanism, such as site-signed operations, can be put in place to enforce
 * this guarantee across the distributed system.
 *
 * It is possible to merge multiple [CausalGraph]s together. This will make sure that all the Yarns
 * are properly merged, and will pick the available Yarn if one is available only in one of the two
 * sites.
 *
 * The particularity of the [CausalGraph] topology is that it lets each operation define an
 * arbitrary amount of causally linked operations.
 *
 * @param O The type of the operations that will be contained in this [CausalGraph].
 * @param S The type of the sites that will be managing the Yarns of this [CausalGraph].
 */
interface CausalGraph<O, S> : Collection<CausalGraphYarn<O, S>> {

    /**
     * Returns the [CausalGraphYarn] instance that corresponds to a particular [site] instance, and
     * creates and returns an empty [CausalGraphYarn] if needed.
     *
     * @param site The instance of the site for which a Yarn should be returned.
     */
    operator fun get(site: S): CausalGraphYarn<O, S>

    /**
     * Merges two immutable instances of a [CausalGraph], and returns them as a new instance of a
     * [CausalGraph]. This will mutate neither of the original instances.
     */
    fun merge(other: CausalGraph<O, S>): CausalGraph<O, S>
}