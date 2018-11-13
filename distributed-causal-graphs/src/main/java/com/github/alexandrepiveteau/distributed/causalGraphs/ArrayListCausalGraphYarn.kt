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

package com.github.alexandrepiveteau.distributed.causalGraphs

import com.github.alexandrepiveteau.functional.monads.map
import com.github.alexandrepiveteau.functional.monads.toMaybe
import com.github.alexandrepiveteau.functional.monads.withDefault

internal class ArrayListCausalGraphYarn<O, S>(private val elements: MutableList<CausalGraphAtom<O, S>> = mutableListOf(), override val site: S) :
        MutableCausalGraphYarn<O, S>, List<CausalGraphAtom<O, S>> by elements {

    override fun insert(operation: O, dependencies: Set<CausalGraphIdentifier<S>>): CausalGraphIdentifier<S> {
        // TODO : Make this code prettier.
        val position = elements.lastOrNull()
                .toMaybe()
                .map { (_, identifier) -> identifier }
                .map { (_, position) -> position + 1 }
                .withDefault(0)
        val identifier = CausalGraphIdentifier(site, position)
        val atom = CausalGraphAtom(operation, identifier, dependencies)
        elements += atom
        return identifier
    }

    override fun merge(other: MutableCausalGraphYarn<O, S>) {

        // Ensure that the two merges Yarns have the same origin site.
        check(site == other.site) { "Merged Yarns must have the same site identifier." }

        val aSequence = elements.asSequence()
        val bSequence = other.asSequence()

        val mergedList = sequenceOf(aSequence, bSequence).flatten()
                .distinctBy { it.identifier }
                .sortedBy { it.identifier.index }
                .toList()

        elements.clear()
        elements.addAll(mergedList)
    }

    override fun merge(other: CausalGraphYarn<O, S>): CausalGraphYarn<O, S> {

        // Ensure that the two merges Yarns have the same origin site.
        check(site == other.site) { "Merged Yarns must have the same site identifier." }

        val aSequence = elements.asSequence()
        val bSequence = other.asSequence()

        val mergedList = sequenceOf(aSequence, bSequence).flatten()
                .distinctBy { it.identifier }
                .sortedBy { it.identifier.index }
                .toMutableList()

        return ArrayListCausalGraphYarn(mergedList, site)
    }

    override fun remove(identifier: CausalGraphIdentifier<S>) {

        // TODO : Make sure this work properly.
        elements.removeAll { (_, i, _) -> i == identifier }
        elements.map { (o, i, d) -> if (d.contains(identifier)) return@map CausalGraphAtom(o, i, d.minus(identifier)) else CausalGraphAtom(o, i, d) }
    }
}