(ns hxgm30.graphdb.plugin.redis.api.factory
  (:require
    [hxgm30.graphdb.plugin.redis.api.db :as redis])
  (:import
    (clojure.lang Keyword)))

(defrecord RedisGraphFactory [
  spec
  pool
  graph-name])

(defn connect
  ([this]
    (connect this (:graph-name this)))
  ([this graph-name]
    (redis/map->RedisGraph (merge this {:graph-name graph-name}))))

(defn destroy
  [this]
  ;; No-op
  )

(def behaviour
  {:connect connect
   :destroy destroy})

(defn create
  ([spec]
    (create spec {}))
  ([spec pool]
    (create spec pool :default))
  ([spec pool ^Keyword graph-name]
    (map->RedisGraphFactory
      {:spec spec
       :pool pool
       :graph-name graph-name})))
