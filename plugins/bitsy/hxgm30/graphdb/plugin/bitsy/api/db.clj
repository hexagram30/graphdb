(ns hxgm30.graphdb.api.impl.bitsy.db
  (:require
    [hxgm30.graphdb.util :as util])
  (:import
    (com.lambdazen.bitsy BitsyGraph)
    (com.lambdazen.bitsy UUID)
    (com.lambdazen.bitsy.wrapper BitsyAutoReloadingGraph))
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/plugin/protocols/db")

(defn -add-edge
  [this src dst label]
  (.addEdge this nil src dst label))

(defn -commit
  [this]
  (.commit this))

(defn -disconnect
  [this]
  (.shutdown this))

(defn -get-edge
  [this id]
  (.getEdge this id))

(defn -get-edges
  [this]
  (into [] (.getEdges this)))

(defn -get-vertices
  [this]
  (into [] (.getVertices this)))

(defn -remove-edge
  [this edge]
  (.removeEdge this edge))

(defn -remove-vertex
  [this vertex]
  (.removeVertex this vertex))

(defn -rollback
  [this]
  (.rollback this))

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
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :backup -backup
   :commit -commit
   :configuration -configuration
   :disconnect -disconnect
   :flush -flush
   :get-edge -get-edge
   :get-edges -get-edges
   :get-vertex -get-vertex
   :get-vertices -get-vertices
   :remove-edge -remove-edge
   :remove-vertex -remove-vertex
   :rollback -rollback
   :show-features -show-features})

(extend BitsyGraph
        GraphDBAPI
        behaviour)

(extend BitsyAutoReloadingGraph
        GraphDBAPI
        behaviour)
