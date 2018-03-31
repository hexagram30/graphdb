(ns hxgm30.graphdb.plugin.redis.api.db
  "Items of interest for implementors:

  * https://github.com/aysylu/loom
  * http://www.vldb.org/pvldb/1/1453965.pdf (Hexastore)
  * https://redis.io/topics/indexes"
  (:require
    [clojure.string :as string]
    [hxgm30.graphdb.plugin.redis.api.queries :as queries]
    [hxgm30.graphdb.plugin.redis.api.schema :as schema]
    [taoensso.carmine :as redis]
    [taoensso.timbre :as log]
    [trifl.java :refer [uuid4]])
  (:refer-clojure :exclude [flush]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Support Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- call
  [this & args]
  (log/debug "Making call to Redis:" args)
  (log/debugf "Native format: %s %s"
              (string/upper-case (name (first args)))
              (string/join " " (rest args)))
  (redis/wcar
    (select-keys this [:spec :pool])
    (redis/redis-call args)))

(defn- create-edge
  ([this]
    (create-edge this {nil nil}))
  ([this attrs]
    (create-edge this nil attrs))
  ([this label attrs]
    (let [id (schema/edge (uuid4))
          normed-attrs (merge attrs (when label {:label label}))
          flat-attrs (mapcat vec normed-attrs)
          result (apply call (concat [this :hmset id] flat-attrs))]
      {:id id
       :result result})))

(defn- create-relation
  [this src-id dst-id edge-id]
  (let [id (schema/relation src-id)
        result (call this :rpush id dst-id)]
    {:id id
     :result result}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(load "/hxgm30/graphdb/plugin/protocols/db")

(defrecord RedisGraph [
  spec
  pool])

(defn- -add-edge
  ([this src-id dst-id]
    (-add-edge this src-id dst-id {nil nil}))
  ([this src-id dst-id attrs]
    (-add-edge this src-id dst-id nil attrs))
  ([this src-id dst-id label attrs]
    (let [multi-result (call this :multi)
          edge-result (create-edge this label attrs)
          relation-result (create-relation this src-id dst-id (:id edge-result))
          exec-result (call this :exec)]
      {:multi multi-result
       :edge edge-result
       :relation relation-result
       :exec exec-result})))

(defn- -add-vertex
  ([this]
    (-add-vertex this {nil nil}))
  ([this attrs]
    (let [id (schema/vertex (uuid4))
          flat-attrs (mapcat vec attrs)
          result (apply call (concat [this :hmset id] flat-attrs))]
      {:id id
       :result result})))

(defn- -add-vertices
  [this props]
  )

(defn- -backup
  [this]
  (call this :bgrewriteaof))

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
  (call this :bgsave))

(defn- -explain
  [this query-str]
  )

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
  )

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

(defn- -vertices
  [this]
  )

(def behaviour
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :backup -backup
   :commit -commit
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
   :show-features -show-features
   :vertices -vertices})

(extend RedisGraph
        GraphDBAPI
        behaviour)
