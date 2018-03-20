(ns hxgm30.graphdb.api.db
  (:require
    [hxgm30.graphdb.api.impl.tinkerpop2.db :as tinkerpop2])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraph
                                           OrientGraphNoTx)))

(defprotocol GraphDBAPI
  (add-edge [this src dst label])
  (add-vertex [this property-map])
  (commit [this])
  (disconnect [this])
  (get-edge [this id])
  (get-edges [this])
  (get-vertex [this id])
  (get-vertices [this])
  (remove-edge [this edge])
  (remove-vertex [this vertex])
  (rollback [this]))

(extend OrientGraph
        GraphDBAPI
        tinkerpop2/behaviour)

(extend OrientGraphNoTx
        GraphDBAPI
        tinkerpop2/behaviour)
