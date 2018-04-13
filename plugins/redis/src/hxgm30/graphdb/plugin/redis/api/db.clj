(ns hxgm30.graphdb.plugin.redis.api.db
  "Items of interest for implementors:

  * https://github.com/aysylu/loom
  * http://www.vldb.org/pvldb/1/1453965.pdf (Hexastore)
  * https://redis.io/topics/indexes"
  (:require
    [clojure.string :as string]
    [hxgm30.graphdb.plugin.redis.api.queries :as queries]
    [hxgm30.graphdb.plugin.redis.api.schema :as schema]
    [hxgm30.graphdb.plugin.redis.api.util :as plugin-util]
    [hxgm30.graphdb.util :as util]
    [taoensso.carmine :as redis]
    [taoensso.timbre :as log]
    [trifl.java :refer [uuid4]])
  (:refer-clojure :exclude [flush]))

(declare get-index)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Support Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- parse-results
  [results]
  (log/trace "Got results:" results)
  (log/trace "Results type:" (type results))
  (condp = results
    "OK" :ok
    results))

(defn- prepare
  [args]
  (log/trace "Got args:" args)
  (apply redis/redis-call args))

(defn- cmd
  [this lib-cmd & args]
  (log/debug "Making carmine call to Redis:" lib-cmd)
  (-> this
      (select-keys [:spec :pool])
      (redis/wcar (apply lib-cmd args))
      (parse-results)))

(defn- pipeline
  [this & cmds]
  (log/debug "Making call(s) to Redis:" cmds)
  (-> this
      (select-keys [:spec :pool])
      (redis/wcar (doall (mapcat prepare cmds)))
      (parse-results)))

(defn- call
  [this & args]
  (log/trace "Using 'call' with args:" args)
  (log/tracef "Wrapping args: [%s] ..." args)
  (pipeline this [args]))

(defn- call-with-cursor
  [this cursor-func cursor]
  (loop [[next-cursor results] (cursor-func cursor)
         acc []]
    (if (= "0" next-cursor)
      (concat acc results)
      (recur (cursor-func next-cursor) results))))

(defn- get-attrs
  ([this id]
    (get-attrs this id 0))
  ([this id cursor]
    (let [results (call-with-cursor
                   this
                   (fn [cursor] (call this :hscan id cursor))
                   cursor)]
      (if (util/tuple? results)
        (util/tuple->map results)
        (util/tuples->map results)))))

(defn find-keys
  [this pattern]
  (if (= "*" pattern)
    (let [msg "Provided pattern would result in expensive query."]
      (log/error msg)
      {:error {:type :bad-query
               :msg msg}})
    (call this :keys pattern)))

(defn- -delete-all
  [this get-all-fn]
  (->> this
       get-all-fn
       (map (fn [x] [:del x]))
       (pipeline this)
       vec))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Support Commands   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- create-edge-cmd
  ([id]
    (create-edge-cmd id {nil nil}))
  ([id attrs]
    (create-edge-cmd id nil attrs))
  ([id label attrs]
    (let [normed-attrs (merge attrs (when label {:label label}))
          flat-attrs (mapcat vec normed-attrs)]
      (concat [:hmset id] flat-attrs))))

(defn- create-relation-cmd
  [this src-id dst-id edge-id]
  (let [id (get-index this :relation src-id)]
    [:rpush id dst-id]))

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
    (let [edge-id (get-index this :edge)
          result (pipeline
                   this
                   [[:multi]
                    (create-edge-cmd edge-id label attrs)
                    (create-relation-cmd this src-id dst-id edge-id)
                    [:exec]])]
      {:id edge-id
       :result result})))

(defn- -add-vertex
  ([this]
    (-add-vertex this {nil nil}))
  ([this attrs]
    (-add-vertex this nil attrs))
  ([this label attrs]
    (let [id (get-index this :vertex)
          normed-attrs (merge attrs (when label {:label label}))
          flat-attrs (mapcat vec normed-attrs)
          result (apply call (concat [this :hmset id] flat-attrs))]
      {:id id
       :result result})))

(defn- -add-vertices
  [this props]
  :forth-coming)

(defn- -backup
  [this]
  (log/infof "%s ..." (call this :bgrewriteaof))
  :ok)

(defn- -commit
  [this]
  :not-implemented)

(defn- -configuration
  [this]
  :not-implemented)

(defn- -get-index
  ([this data-type]
    (-get-index this data-type (uuid4)))
  ([this data-type id]
    (case data-type
      :edge (schema/edge id)
      :relation (schema/relation id)
      :vertex (schema/vertex id))))

(defn- -disconnect
  [this]
  :not-implemented)

(defn- -dump
  [this]
  (log/infof "%s ..." (call this :bgsave))
  :ok)

(defn- -explain
  [this query-str]
  :not-implemented)

(defn- -flush
  [this]
  :not-implemented)

(defn- -get-edge
  ([this id]
    (-get-edge this id 0))
  ([this id cursor]
    {id (util/list->map (call this :hgetall id))}))

(defn- -get-edges
  [this]
  (find-keys this (get-index this :edge "*")))

(defn- -get-relations
  [this]
  (find-keys this (get-index this :relation "*")))

(defn- -get-vertex
  ([this id]
    (-get-vertex this id 0))
  ([this id cursor]
    {id (util/list->map (call this :hgetall id))}))

(defn- -get-vertex-relations
  [this id]
  (call this :lrange (get-index this :relation id) 0 -1))

(defn- -get-vertices
  [this]
  (find-keys this (get-index this :vertex "*")))

(defn- -get-vertices-relations
  [this ids]
  (->> ids
       (map (fn [x] [:lrange (get-index this :relation x) 0 -1]))
       (pipeline this)
       vec))

(defn- -remove-edge
  [this id]
  (call this :del id))

(defn- -remove-edges
  [this]
  (-delete-all this -get-edges))

(defn- -remove-relation
  [this relation-id vertex-id]
  (vec (call this :lrem relation-id 0 vertex-id)))

(defn- -remove-relations
  ([this]
    (-delete-all this -get-relations))
  ([this vertex-id]
    (let [relation-id (get-index this :relation vertex-id)]
      (->> vertex-id
           (-get-vertex-relations this)
           (map (partial remove-relation this relation-id))
           vec))))

(defn- -remove-vertex
  [this id]
  (call this :del id))

(defn- -remove-vertices
  [this]
  (-delete-all this -get-vertices))

(defn- -rollback
  [this]
  :not-implemented)

(defn- -show-features
  [this]
  :not-implemented)

(defn- -edges
  ([this]
    (-edges this (get-edges this)))
  ([this ids]
    (->> ids
         (map (partial get-edge this))
         (reduce merge))))

(defn- -relations
  ([this]
    (-relations this (get-edges this)))
  ([this ids]
    (->> ids
         (map (partial get-edge this))
         vec)))

(defn- -vertices
  ([this]
    (-vertices this (get-vertices this)))
  ([this ids]
    (->> ids
         (map (partial get-vertex this))
         (reduce merge))))

(def behaviour
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :backup -backup
   :commit -commit
   :configuration -configuration
   :disconnect -disconnect
   :dump -dump
   :edges -edges
   :explain -explain
   :flush -flush
   :get-edge -get-edge
   :get-edges -get-edges
   :get-index -get-index
   :get-relations -get-relations
   :get-vertex -get-vertex
   :get-vertex-relations -get-vertex-relations
   :get-vertices -get-vertices
   :get-vertices-relations -get-vertices-relations
   :relations -relations
   :remove-edge -remove-edge
   :remove-edges -remove-edges
   :remove-relation -remove-relation
   :remove-relations -remove-relations
   :remove-vertex -remove-vertex
   :remove-vertices -remove-vertices
   :rollback -rollback
   :show-features -show-features
   :vertices -vertices})

(extend RedisGraph
        GraphDBAPI
        behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Non-API Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn drop-data
  [this]
  (call this :flushdb))

(defn latency-setup
  ([this]
    (latency-setup this 100))
  ([this milliseconds]
    (call this :config :set "latency-monitor-threshold" milliseconds)))

(defn latency-latest
  [this]
  (call this :latency :latest))

(defn latency-doctor
  [this]
  (print (call this :latency :doctor))
  :ok)

(defn slowlog
  ([this]
    (call this :slowlog :get))
  ([this count]
    (call this :slowlog :get count)))
