(ns hxgm30.graphdb.api.db
  (:require
    [hxgm30.graphdb.api.impl.bitsy.db :as bitsy]
    [hxgm30.graphdb.api.impl.tinkerpop2.db :as tinkerpop2]
    [hxgm30.graphdb.util :as util]
    [taoensso.timbre :as log])
  (:import
    (com.lambdazen.bitsy BitsyGraph)
    (com.lambdazen.bitsy.wrapper BitsyAutoReloadingGraph)
    (com.tinkerpop.blueprints.impls.orient OrientGraph
                                           OrientGraphNoTx))
  (:refer-clojure :exclude [flush]))

(defprotocol GraphDBAPI
  (add-edge [this src dst label])
  (add-vertex [this] [this property-map])
  (backup [this] [this path])
  (commit [this])
  (configuration [this])
  (cypher [this query-str])
  (disconnect [this])
  (dump [this])
  (explain [this query-str])
  (flush [this])
  (get-edge [this id])
  (get-edges [this])
  (get-vertex [this id])
  (get-vertices [this])
  (remove-edge [this edge])
  (remove-vertex [this vertex])
  (rollback [this])
  (show-features [this]))

(extend OrientGraph
        GraphDBAPI
        tinkerpop2/behaviour)

(extend OrientGraphNoTx
        GraphDBAPI
        tinkerpop2/behaviour)

(extend BitsyGraph
        GraphDBAPI
        bitsy/behaviour)

(extend BitsyAutoReloadingGraph
        GraphDBAPI
        bitsy/behaviour)

(try
  (when-let [backend-class (util/import-class 'hxgm30.graphdb.plugin.redis.api.db.RedisGraph)]
    (require '[hxgm30.graphdb.plugin.redis.api.db])
    (when-let [backend-ns (util/require-ns 'hxgm30.graphdb.plugin.redis.api.db)]
      (extend backend-class
              GraphDBAPI
              (ns-resolve backend-ns 'behaviour))
      (log/debug "Extended Redis db.")))
  (catch Exception _
    (log/debug "Redis backend is not enabled; not extending.")))
