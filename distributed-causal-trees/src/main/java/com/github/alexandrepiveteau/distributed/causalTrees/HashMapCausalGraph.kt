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

import com.github.alexandrepiveteau.functional.monads.fold
import com.github.alexandrepiveteau.functional.monads.map
import com.github.alexandrepiveteau.functional.monads.toMaybe
import com.github.alexandrepiveteau.functional.monads.zip2

internal class HashMapCausalGraph<O, S>(private val yarns: MutableMap<S, MutableCausalGraphYarn<O, S>> = mutableMapOf()) : MutableCausalGraph<O, S> {

    override val size: Int
        get() = yarns.size

    override fun get(site: S): MutableCausalGraphYarn<O, S> =
            yarns.getOrPut(site) { ArrayListCausalGraphYarn(site = site) }

    override fun merge(other: CausalGraph<O, S>): CausalGraph<O, S> {
        val yarns: MutableMap<S, MutableCausalGraphYarn<O, S>> = mutableMapOf()
        val sites: Set<S> = yarns.keys.toSet() + other.asSequence().map { it.site }.toSet()

        for (site in sites) {
            val aYarnMaybe = yarns[site].toMaybe()
            val bYarnMaybe = other[site].toMaybe()
            val mergedYarnMaybe = aYarnMaybe.zip2(bYarnMaybe).map { (a, b) -> a.merge(b) }
            mergedYarnMaybe.fold({ yarn ->
                val elements = yarn.toMutableList()
                yarns[site] = ArrayListCausalGraphYarn(elements, site)
            }, { /* Ignore. */ })
        }

        return HashMapCausalGraph(yarns)
    }

    override fun merge(other: MutableCausalGraph<O, S>) {
        val sites = yarns.keys.toSet() + other.asSequence().map { it.site }
        sites.forEach { site ->
            val otherSiteYarn: MutableCausalGraphYarn<O, S> = other[site]
            this[site].merge(otherSiteYarn)
        }
    }

    override fun contains(element: CausalGraphYarn<O, S>): Boolean {
        return yarns.values.contains(element)
    }

    override fun containsAll(elements: Collection<CausalGraphYarn<O, S>>): Boolean {
        return yarns.values.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return yarns.isEmpty()
    }

    override fun iterator(): Iterator<CausalGraphYarn<O, S>> {
        return yarns.values.iterator()
    }

}