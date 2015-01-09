# How to Compose Zones

If you want to include a zone in another zone, check out this example.

## Start two REPLS in two different terminals

Start REPL 1 for the original zone with:

```bash
export ZONE_ID=1001
export NODE_PORT=3001
export DYNAMO_TABLE_NAME=fondo_zone_1001
lein repl
```

Add some data with:

```clojure
(put-value {:name "StoryCorps" :uri "http://storycorps.org/"})
```

Start REPL 2 for the composing zone with:

```bash
export ZONE_ID=1002
export NODE_PORT=3002
export DYNAMO_TABLE_NAME=fondo_zone_1002
lein repl
```

```clojure
(put-value {:name "RadioLab" :uri "http://www.radiolab.org/"})
```

Use Cases for including a zone:

* Mirroring the zone
* 'Trusting' a zone (sort of)
* Making a zone available
* Offering an aggregation of other zones
* A 'curation' of other zones (which raises the question of selective inclusion).
* A university (as a whole) wants to include its department zones. So this
implies that all underlying data is kept; so, no removal of underlying data.

Questions:

* When does the 'mirroring' happen in the lifecycle?
* Is the 'mirroring' ongoing?
* Does the mirroring have to be complete or can ID's be excluded?
* What if conflicts happen later (e.g. after the original 'connection'?)

Decisions:

* Metadata is probably cheap.
* If run a compositional zone, you are deciding to get everything. If this turns
out to be a problem, we can think about ways to filter.
* We can make mirroring be a one-off thing. That way a zone has the flexibility
  to schedule it as it wishes. That suggests it will be a function. It will have
  to deal with conflict as it arises.

What is the expectation to a client?

* Portland State tells its students that it makes all data available at
Zone 1002.
* Dr. Who works in the Psych department. He publishes to his local department
  server. He expects it to show up there and at the university level. He
  publishes to zone 1001.
* Dr. Dan works in the Poly Sci department. He publishes to his local department
  and expects the same as Dr. Who. Publishes to zone 1001.
* Student Sam wants to download everything, not knowing about where it comes
from. He pulls from zone 1002.
* President Paul publishes university-wide to Zone 1002.
* Student Karl want to download from the Poly Sci department.

Design Iteration #1:

```clojure
(include-zone {:node-uri "http://localhost:3001"})
```

Problem: this seems to be a batch operation.

How do we split it apart...?

Questions:
* Can we rely on an ordering of some sort?
* Is the ordering stable?
* If it is, then 'pagination' will be reliable.

We've decided that each node will use its own timestamp.

And a 'mirroring' node talks to some number of other nodes. If it wants, it can
track a timestamp for each node. Then it can recent a bracketing window of data
added in some range of time.

Design Iteration #2:

```clojure
(include-zone {:node-uri "http://localhost:3001"
               :since    #inst 2015-01-0900:000000
               :batch-size 1000})
               ```

Returns an ordered set of results up to a maximum batch size.

This allows the client to page through the results however it likes.

TODO:

* Implement the above
* Add timestamps somewhere.

TODO: We'll want to figure out conflict resolution.

Use cases:

* Original has a key that clashes with new, or vice versa.
* How do we resolve this situation?
* By timestamp?
* Do we prefer the original?
* Do we prefer the 'local'?
* Are siblings created?

Down the road:

* What about nodes in a zone...? It may just work.
* Functional transformations?
* Node discovery?
* Latency-based replication?
