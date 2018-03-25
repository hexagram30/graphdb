(ns hxgm30.graphdb.api.impl.redis.db
  (:require
    [taoensso.carmine :as redis])
  (:refer-clojure :exclude [flush]))

(defrecord RedisGraph [
  spec
  pool
  graph-name])


(defn- -call
  [this & args]
  (redis/wcar
    (select-keys this [:spec :pool])
    (redis/redis-call args)))

(defn cypher
  [this query-str]
  (-call this :graph.query (name (:graph-name this)) query-str))

(defn add-edge
  [this]
  )

(defn add-vertex
  [this props]
  )

(defn backup
  [this]
  (-call this :bgrewriteaof))

(defn commit
  [this]
  )

(defn configuration
  [this]
  )

(defn disconnect
  [this]
  )

(defn dump
  [this]
  (-call this :bgsave))

(defn explain
  [this query-str]
  (print (-call this :graph.explain (name (:graph-name this)) query-str))
  :ok)

(defn flush
  [this]
  )

(defn get-edge
  [this]
  )

(defn get-edges
  [this]
  )

(defn get-vertex
  [this id]
  )

(defn get-vertices
  [this]
  )

(defn remove-edge
  [this]
  )

(defn remove-vertex
  [this]
  )

(defn rollback
  [this]
  )

(defn show-features
  [this]
  )

(def behaviour
  {:add-edge add-edge
   :add-vertex add-vertex
   :backup backup
   :commit commit
   :cypher cypher
   :configuration configuration
   :disconnect disconnect
   :explain explain
   :flush flush
   :get-edge get-edge
   :get-edges get-edges
   :get-vertex get-vertex
   :get-vertices get-vertices
   :remove-edge remove-edge
   :remove-vertex remove-vertex
   :rollback rollback
   :show-features show-features})