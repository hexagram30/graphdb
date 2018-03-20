(ns hxgm30.graphdb.api.core
  "We decided not to use one of the Clojure libraries for TinkerPop or OrientDB
  for the following reasons:

  * Most have grown out of date and we didn't want to tie ourselves into a
    solution there's a good chance of having to step away from.
  * After further looking at 7bridges' library, we're not sold on their design
    or idiom.
  * The Clojure Ogre library focuses only on graph traversal, not graph
    manipulation -- which we need way more than traversal
  * OrientDB is currently transitioning from TinkerPop2 in it's 2.2.x series to
    TinkerPop3 in its 3.x series -- however, this hasn't landed yet.

  So here we are, cobbling together something we can use that has the features
  we need, but from which we can bail as soon as something better comes along.

  This namespace is really just used to consolidate this note. See it's sibling
  namespaces for actual code.")
