(ns hxgm30.graphdb.api.impl.bitsy.db
  (:require
    [hxgm30.graphdb.api.impl.tinkerpop2.db :as base]
    [hxgm30.graphdb.util :as util])
  (:import
    (com.lambdazen.bitsy UUID)
    (com.lambdazen.bitsy BitsyGraph)
    (com.lambdazen.bitsy.wrapper BitsyAutoReloadingGraph))
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/api/impl/tinkerpop2/db")

(defn- -add-vertex
  [this props]
  (.addVertex this (object-array (mapcat vec (util/keys->strs props)))))

(defn- -backup
  [this ^String path]
  (.backup this path))

(defn- -configuration
  [this]
  (let [cfg (.configuration this)]
    (->> cfg
         (.getKeys)
         (iterator-seq)
         (map (fn [x] [(keyword x) (.getProperty cfg x)]))
         (into {}))))

(defn- -flush
  [this]
  (.flushTxLog this))

(defn- -get-vertex
  [this ^String uuid]
  (.getVertex (.tx this) (UUID/fromString uuid)))

(defn- -show-features
  [this]
  (print (str (.features this))))

(def behaviour
  {:add-edge base/add-edge
   :add-vertex -add-vertex
   :backup -backup
   :commit base/commit
   :configuration -configuration
   :disconnect base/disconnect
   :flush -flush
   :get-edge base/get-edge
   :get-edges base/get-edges
   :get-vertex -get-vertex
   :get-vertices base/get-vertices
   :remove-edge base/remove-edge
   :remove-vertex base/remove-vertex
   :rollback base/rollback
   :show-features -show-features})

(load "/hxgm30/graphdb/api/protocols/db")

(extend BitsyGraph
        GraphDBAPI
        behaviour)

(extend BitsyAutoReloadingGraph
        GraphDBAPI
        behaviour)
