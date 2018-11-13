# distributed-kotlin

[![](https://jitpack.io/v/alexandrepiveteau/distributed-kotlin.svg)](https://jitpack.io/#alexandrepiveteau/distributed-kotlin)

This repository contains some utilies for distributed systems in the Kotlin programming language.
The OSS license can be found in the LICENSE.md file of the repository.

## Installation
This library is available on [JitPack.io](https://jitpack.io/#alexandrepiveteau/distributed-kotlin). Make
sure to add the following Maven repository in your root **build.gradle** file :

```groovy
allprojects {
	repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

You can now add the library modules in your application **build.gradle** file :

```groovy
dependencies {
    implementation "com.github.alexandrepiveteau.distributed-kotlin:distributed-causal-graphs:1.0.1"
	implementation "com.github.alexandrepiveteau.distributed-kotlin:distributed-cvrdts:1.0.1"
	implementation "com.github.alexandrepiveteau.distributed-kotlin:distributed-woot:1.0.1"
}
```

## Usage
The library contains the following modules :

- **distributed-causal-graphs** - An implementation of **causal graphs**, including `CausalGraph<O, S>` and `CausalGraphYarn<O, S>` with their mutable counterparts.
- **distributed-cvrdts** - An implementation of some popular **CvRDTs**, including `GSet<T>`, `PNSet<T>` and `MCSet<T>`.
- **distributed-woot** - An implementation of the **Woot** linear data structure.

### distributed-cvrdts

The library offers multiple **CvRDT** data structures. Each data type is a state-based CRDT. Essentially, the `GSet<T>`, `PNSet<T>` and `MCSet<T>` classes all implement the `Set<T>` interface. Each of these data type is immutable, and multiple CRDTs of the same type can be merged by using their `merge(...)` function.

Here is an example of usage for the `MCSet<T>` data type :

```kotlin
var first: MCSet<String> = emptyMCSet()
var second: MCSet<String> = emptyMCSet()

first += "Alice"
first += "Bob"
first -= "Bob"
second += "Bob"
second += "Charlie"

val merged = first.merge(second) // Contains "Alice" and "Charlie".
```