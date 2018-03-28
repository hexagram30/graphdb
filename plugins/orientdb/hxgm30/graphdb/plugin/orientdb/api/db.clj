(ns hxgm30.graphdb.plugin.orientdb.api.db
  "This is the implementation that's intended to be used with OrientDB v 2.2.x.

  Resources for this implementation:
  * https://orientdb.com/docs/2.2.x/Graph-Database-Tinkerpop.html
  * https://orientdb.com/docs/2.2.x/Graph-VE.html
  * https://orientdb.com/javadoc/2.2.x/index.html?com/tinkerpop/blueprints/impls/orient/OrientGraphNoTx.html
  * https://orientdb.com/javadoc/2.2.x/index.html?com/tinkerpop/blueprints/impls/orient/OrientGraphTx.html"
  (:require
    [hxgm30.graphdb.util :as util])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraph
                                           OrientGraphNoTx))
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/plugin/protocols/db")

(defn- -add-edge
  [this src dst label]
  (.addEdge this nil src dst label))

(defn- -add-vertex
  [this props]
  (.addVertex this (util/keys->strs props)))

(defn- -commit
  [this]
  (.commit this))

(defn- -disconnect
  [this]
  (.shutdown this))

(defn- -get-edge
  [this id]
  (.getEdge this id))

(defn- -get-edges
  [this]
  (into [] (.getEdges this)))

(defn- -get-vertex
  [this id]
  (.getVertex this id))

(defn- -get-vertices
  [this]
  (into [] (.getVertices this)))

(defn- -remove-edge
  [this edge]
  (.removeEdge this edge))

(defn- -remove-vertex
  [this vertex]
  (.removeVertex this vertex))

(defn- -rollback
  [this]
  (.rollback this))

(def behaviour
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :commit -commit
   :disconnect -disconnect
   :get-edge -get-edge
   :get-edges -get-edges
   :get-vertex -get-vertex
   :get-vertices -get-vertices
   :remove-edge -remove-edge
   :remove-vertex -remove-vertex
   :rollback -rollback})

(extend OrientGraph
        GraphDBAPI
        behaviour)

(extend OrientGraphNoTx
        GraphDBAPI
        behaviour)
