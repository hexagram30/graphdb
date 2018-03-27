(ns hxgrm30.graphdb.plugin.orientdb.api.db
  "This is the implementation that's intended to be used with OrientDB v 2.2.x.

  Resources for this implementation:
  * https://orientdb.com/docs/2.2.x/Graph-Database-Tinkerpop.html
  * https://orientdb.com/docs/2.2.x/Graph-VE.html
  * https://orientdb.com/javadoc/2.2.x/index.html?com/tinkerpop/blueprints/impls/orient/OrientGraphNoTx.html
  * https://orientdb.com/javadoc/2.2.x/index.html?com/tinkerpop/blueprints/impls/orient/OrientGraphTx.html"
  (:require
    [hxgm30.graphdb.util :as util])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraph
                                           OrientGraphNoTx)))

(load "/hxgm30/graphdb/api/impl/tinkerpop2/db")

(def behaviour tinkerpop2-behaviour)

(load "/hxgm30/graphdb/api/protocols/db")

(extend OrientGraph
        GraphDBAPI
        tinkerpop2/behaviour)

(extend OrientGraphNoTx
        GraphDBAPI
        tinkerpop2/behaviour)
