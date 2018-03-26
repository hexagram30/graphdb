(ns hxgm30.graphdb.api.factory
  (:require
    [hxgm30.graphdb.api.impl.bitsy.factory :as bitsy]
    [hxgm30.graphdb.api.impl.orientdb.factory :as orientdb]
    [hxgm30.graphdb.util :as util]
    [taoensso.timbre :as log])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory)
    (hxgm30.graphdb.api.impl.bitsy.factory BitsyGraphFactory)))

(defprotocol DBFactoryAPI
  (connect [this] [this opts])
  (destroy [this]))

(extend OrientGraphFactory
        DBFactoryAPI
        orientdb/behaviour)

(extend BitsyGraphFactory
        DBFactoryAPI
        bitsy/behaviour)

(try
  (when-let [backend-class (util/import-class 'hxgm30.graphdb.plugin.redis.api.factory.RedisGraphFactory)]
    (require '[hxgm30.graphdb.plugin.redis.api.factory])
    (when-let [backend-ns (util/require-ns 'hxgm30.graphdb.plugin.redis.api.factory)]
      (extend backend-class
              DBFactoryAPI
              (symbol "hxgm30.graphdb.plugin.redis.api.factory" "behaviour"))
      (log/debug "Extended Redis factory.")))
  (catch Exception _
    (log/debug "Redis backend is not enabled; not extending.")))

(defn create
  [factory-type spec]
  (case factory-type
    :orientdb (orientdb/create spec)
    :bitsy (bitsy/create spec)
    ;:redis (redis/create spec)
    ))
