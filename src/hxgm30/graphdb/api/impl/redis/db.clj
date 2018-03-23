(ns hxgm30.graphdb.api.impl.redis.db
  (:require
    [taoensso.carmine :as redis])
  (:refer-clojure :exclude [flush]))

(defrecord RedisGraph [
  spec
  pool
  graph-name])


(defn cypher
  [this query-str]
  (redis/wcar
    (select-keys this [:spec :pool])
    (redis/redis-call [:graph.query (name (:graph-name this)) query-str])))

(defn add-edge
  [this]
  )

(defn add-vertex
  [this props]
  )

(defn backup
  [this path]
  )

(defn commit
  [this]
  )

(defn configuration
  [this]
  )

(defn disconnect
  [this]
  )

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
   :flush flush
   :get-edge get-edge
   :get-edges get-edges
   :get-vertex get-vertex
   :get-vertices get-vertices
   :remove-edge remove-edge
   :remove-vertex remove-vertex
   :rollback rollback
   :show-features show-features})
