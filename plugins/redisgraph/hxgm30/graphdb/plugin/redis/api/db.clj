(ns hxgm30.graphdb.plugin.redisgraph.api.db
  "Items of interest for implementors:

  * http://redisgraph.io/commands/
  * https://s3.amazonaws.com/artifacts.opencypher.org/openCypher9.pdf
  * https://github.com/ptaoussanis/carmine
  * https://github.com/ptaoussanis/carmine/blob/master/src/taoensso/carmine.clj
  * http://download.redis.io/redis-stable/redis.conf"
  (:require
    [clojure.string :as string]
    [hxgm30.graphdb.plugin.redisgraph.api.queries :as queries]
    [hxgm30.graphdb.plugin.redisgraph.api.util :as util]
    [taoensso.carmine :as redis]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/plugin/protocols/db")

(defrecord RedisGraph [
  spec
  pool
  graph-name])

(defn- -call
  [this & args]
  (log/debug "Making call to Redis:" args)
  (log/debugf "Native format: %s %s \"%s\""
              (string/upper-case (name (first args)))
              (name (second args))
              (nth args 2))
  (redis/wcar
    (select-keys this [:spec :pool])
    (redis/redis-call args)))

(defn- -cypher
  [this query-str]
  (-call this :graph.query (name (:graph-name this)) query-str))

(defn- -add-edge
  [this]
  )

(defn- -add-vertices
  [this nodes-props]
  (-cypher this))

(defn- -add-vertex
  ([this]
    (-cypher this queries/create-simple-node))
  ([this props]
    (-add-vertex this (:label props) (dissoc props :label)))
  ([this label props]
    (-cypher)))

(defn- -backup
  [this]
  (-call this :bgrewriteaof))

(defn- -commit
  [this]
  )

(defn- -configuration
  [this]
  )

(defn- -disconnect
  [this]
  )

(defn- -dump
  [this]
  (-call this :bgsave))

(defn- -explain
  [this query-str]
  (print (-call this :graph.explain (name (:graph-name this)) query-str))
  :ok)

(defn- -flush
  [this]
  )

(defn- -get-edge
  [this]
  )

(defn- -get-edges
  [this]
  )

(defn- -get-vertex
  [this id]
  )

(defn- -get-vertices
  [this]
  (-cypher this queries/match-all-nodes))

(defn- -remove-edge
  [this]
  )

(defn- -remove-vertex
  [this]
  )

(defn- -rollback
  [this]
  )

(defn- -show-features
  [this]
  )

(def behaviour
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :backup -backup
   :commit -commit
   :cypher -cypher
   :configuration -configuration
   :disconnect -disconnect
   :explain -explain
   :flush -flush
   :get-edge -get-edge
   :get-edges -get-edges
   :get-vertex -get-vertex
   :get-vertices -get-vertices
   :remove-edge -remove-edge
   :remove-vertex -remove-vertex
   :rollback -rollback
   :show-features -show-features})

(extend RedisGraph
        GraphDBAPI
        behaviour)
