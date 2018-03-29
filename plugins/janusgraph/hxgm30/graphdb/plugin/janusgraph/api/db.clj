(ns hxgm30.graphdb.plugin.janusgraph.api.db
  (:require
    [hxgm30.graphdb.util :as util])
  (:import
    (org.janusgraph.graphdb.database StandardJanusGraph))
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/plugin/protocols/db")

(defn -add-edge
  [this src dst label]
  (.addEdge this nil src dst label))

(defn- -add-vertex
  [this props]
  (.addVertex this))

(defn- -backup
  [this ^String path]
  (.backup this path))

(defn- -closed?
  [this]
  (.isClosed this))

(defn- -commit
  [this]
  (.commit this))

(defn- -configuration
  [this]
  (.getConfiguration this))

(defn -disconnect
  [this]
  (.close this))

(defn- -features
  [this]
  (print (str (.features this)))
  :ok)

(defn- -flush
  [this]
  (.flush this))

(defn -get-edge
  [this id]
  (.getEdge this id))

(defn -get-edges
  [this]
  (into [] (.getEdges this)))

(defn- -get-vertex
  [this ^String uuid]
  (.getVertex this))

(defn -get-vertices
  [this]
  (into [] (.getVertices this)))

(defn- -graph-name
  [this]
  (.getGraphName this))

(defn- -open?
  [this]
  (.isOpen this))

(defn -remove-edge
  [this edge]
  (.removeEdge this edge))

(defn -remove-vertex
  [this vertex]
  (.removeVertex this vertex))

(defn -rollback
  [this]
  (.rollback this))

(defn- -show-features
  [this]
  (print (str (.features this))))

(def behaviour
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :backup -backup
   :closed? -closed?
   :commit -commit
   :configuration -configuration
   :disconnect -disconnect
   :features -features
   :flush -flush
   :get-edge -get-edge
   :get-edges -get-edges
   :get-vertex -get-vertex
   :get-vertices -get-vertices
   :graph-name -graph-name
   :open? -open?
   :remove-edge -remove-edge
   :remove-vertex -remove-vertex
   :rollback -rollback
   :show-features -show-features})

(extend StandardJanusGraph
        GraphDBAPI
        behaviour)
