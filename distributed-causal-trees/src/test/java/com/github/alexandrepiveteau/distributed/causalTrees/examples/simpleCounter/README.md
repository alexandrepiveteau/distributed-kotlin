# Simple Counter
This module contains an example implementation of a data structure built on top of the CausalTree
abstraction. The chosen data structure is a simple **increasing** and **decreasing** **counter**.

## Operations
The primitive operations for this data structure are the `Increase` and `Decrease` operations. The
operations are all caused by the `Root` node, because there is no complex chaining of operations
needed to represent the behavior of the counter.

Each site is free to issue the operations he wants, no matter what the current state of the
CausalTree is. No garbage collection mechanism is put in place, as we need an increasing
semi-lattice which is best represented by the operations chains. Further optimizations like
operation coalescing are left out of the scope of this example.