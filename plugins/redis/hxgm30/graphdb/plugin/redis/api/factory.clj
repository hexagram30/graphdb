(ns hxgm30.graphdb.plugin.redis.api.factory
  (:require
    [hxgm30.graphdb.plugin.redis.api.db :as redis])
  (:import
    (clojure.lang Keyword))
  (:refer-clojure :exclude [drop]))

(load "/hxgm30/graphdb/plugin/protocols/factory")

(defrecord RedisFactory [
  spec
  pool])

(defn- -connect
  [this]
  (redis/map->RedisGraph this))

(defn- -destroy
  [this]
  ;; No-op
  )

(def behaviour
  {:connect -connect
   :destroy -destroy})

(extend RedisFactory
        DBFactoryAPI
        behaviour)

(defn create
  ([spec]
    (create spec {}))
  ([spec pool]
    (map->RedisFactory
      {:spec spec
       :pool pool})))
