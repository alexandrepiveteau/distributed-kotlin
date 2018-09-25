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

package ch.alexandrepiveteau.external.culturedCode.challenge

import com.github.alexandrepiveteau.functional.monads.*

/**
 * A program that creates a simple dictionary, a list of atomic map operations and applies them
 * to the dictionary.
 *
 * Some pairs of operations can be optimized, if they are applied to the same key. This simplistic
 * table shows the optimization rules :
 *
 * - Create = Create
 * - Delete = Delete
 * - Update = Update or NoOp (chose to always update with value)
 * - Create + Create = ERROR or Create (chose to create with second value)
 * - Create + Delete = NoOp
 * - Create + Update = Create
 * - Delete + Create = Update or NoOp (chose to always update with second value)
 * - Delete + Delete = ERROR or Delete (chose to delete with key)
 * - Update + Create = ERROR (chose to Update with second value)
 * - Update + Delete = Delete
 * - Update + Update = Update or NoOp (chose to always update with second value)
 *
 * These optimization rules will be applied in the order of traversal of the list of operations.
 * To perform this, an internal HashMap structure will be used, with O(1) access time.
 *
 * Note that I used a simple library of my own that offers the Maybe<T> / Option<T> monad, to
 * make a few methods safer and easier to write. An early version of the library is on Github,
 * under the url https://github.com/alexandrepiveteau/functional-kotlin
 *
 * @author Alexandre Piveteau
 */
fun main(args: Array<String>) {

    val dictionary = mutableMapOf("Bob" to 10, "Claire" to 45)
    val operations = listOf(AtomicMapOperation.Create("Alice", 13), AtomicMapOperation.Update("Bob", 29), AtomicMapOperation.Delete("Claire"))

    // Optimizer / Combining step.
    applyAtomic(operations = operations, toMutableMap = dictionary)

    // Woohoo.
    println(dictionary)
}

/**
 * A sealed class representing the different atomic operations that can be created an used on an
 * instance of a [Map]. Atomicity means that two [AtomicMapOperation]s will not necessarily be
 * able to be combined into a single instance of another [AtomicMapOperation].
 *
 * @param K The type of the keys used for each of the [AtomicMapOperation]s.
 * @param V The type of the value used for each of the [AtomicMapOperation]s.
 */
sealed class AtomicMapOperation<out K, V>(val key: K) {
    class Create<out K, V>(key: K, val value: V): AtomicMapOperation<K, V>(key)
    class Delete<out K, V>(key: K): AtomicMapOperation<K, V>(key)
    class Update<out K, V>(key: K, val value: V): AtomicMapOperation<K, V>(key)
}

/**
 * Combines an instance of an [AtomicMapOperation] with another instance of an [AtomicMapOperation],
 * without considering the context of the [Map] before. The semantics will always be defined as
 * being optimized and as if all the content had been previously transformed into some operations
 * already.
 *
 * Runs in O(1).
 */
fun <K, V> AtomicMapOperation<K, V>.combineWith(otherWithSameKey: AtomicMapOperation<K, V>): Maybe<AtomicMapOperation<K ,V>> {
    // Check preconditions that the keys are the same.
    check(this.key == otherWithSameKey.key) { "You should not combine operations with different keys. Shame shame shame :)" }
    return when(this) {
        is AtomicMapOperation.Create ->
            when(otherWithSameKey) {
                is AtomicMapOperation.Create -> maybeOf(otherWithSameKey)
                is AtomicMapOperation.Delete -> emptyMaybe()
                is AtomicMapOperation.Update -> maybeOf(AtomicMapOperation.Create(otherWithSameKey.key, otherWithSameKey.value))
            }
        is AtomicMapOperation.Delete ->
            when(otherWithSameKey) {
                is AtomicMapOperation.Create -> maybeOf(AtomicMapOperation.Update(otherWithSameKey.key, otherWithSameKey.value))
                is AtomicMapOperation.Delete -> maybeOf(this)
                is AtomicMapOperation.Update -> emptyMaybe()
            }
        is AtomicMapOperation.Update ->
            when(otherWithSameKey) {
                is AtomicMapOperation.Create -> maybeOf(otherWithSameKey)
                is AtomicMapOperation.Delete -> maybeOf(otherWithSameKey)
                is AtomicMapOperation.Update -> maybeOf(otherWithSameKey)
            }
    }
}

/**
 * Given a [List] of [AtomicMapOperation]s, returns another instance of a [List] of
 * [AtomicMapOperation] that has been optimized.
 *
 * Runs in time O(n) and takes O(n + k) space.
 */
fun <K, V> optimize(operations: List<AtomicMapOperation<K, V>>): List<AtomicMapOperation<K, V>> {
    val lastOperationMutableMap: MutableMap<K, Maybe<AtomicMapOperation<K, V>>> = mutableMapOf()
    for (operation in operations) {
        val previousOperationMaybe = lastOperationMutableMap[operation.key] ?: emptyMaybe()
        val optimizedOperation = when (previousOperationMaybe) {
            is Maybe.Some -> operation.combineWith(previousOperationMaybe.just).withDefault(operation)
            is Maybe.None -> operation
        }
        lastOperationMutableMap[optimizedOperation.key] = maybeOf(optimizedOperation)
    }
    return lastOperationMutableMap.values.mapNotNull { it as? Maybe.Some }.map { it.just }
}

/**
 * Applies a [List] of [AtomicMapOperation]s to a [MutableMap] instance. This will usually be the
 * final integration step of the [AtomicMapOperation]s.
 *
 * This method internally uses the [optimize] method to reduce the number of [AtomicMapOperation]s
 * that have to be performed. Obviously, an alternative method not using the [optimize] would be
 * trivial to deduce.
 *
 * Runs in time O(k + n).
 */
fun <K, V> applyAtomic(operations: List<AtomicMapOperation<K, V>>, toMutableMap: MutableMap<K, V>) {
    val allOperations = toMutableMap.entries.toList().map { (k, v) -> AtomicMapOperation.Create(k, v) } + operations
    for (operation in allOperations) {
        when (operation) {
            is AtomicMapOperation.Create -> toMutableMap[operation.key] = operation.value
            is AtomicMapOperation.Delete -> toMutableMap -= operation.key
            is AtomicMapOperation.Update -> toMutableMap[operation.key] = operation.value
        }
    }
}