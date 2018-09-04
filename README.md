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
	implementation "com.github.alexandrepiveteau.distributed-kotlin:distributed-cvrdts:0.1.0"
	implementation "com.github.alexandrepiveteau.distributed-kotlin:distributed-woot:0.1.0"
}
```