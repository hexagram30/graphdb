(ns hxgm30.graphdb.plugin.janusgraph.api.db
  "* https://static.javadoc.io/org.janusgraph/janusgraph-core/0.2.0/org/janusgraph/graphdb/tinkerpop/JanusGraphBlueprintsGraph.html
   * https://static.javadoc.io/org.janusgraph/janusgraph-core/0.2.0/org/janusgraph/graphdb/database/StandardJanusGraph.html"
  (:require
    [hxgm30.graphdb.util :as util]
    [taoensso.timbre :as log])
  (:import
    (org.janusgraph.graphdb.database StandardJanusGraph))
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/plugin/protocols/db")

(defn -add-edge
  [this src dst label]
  (.addEdge this nil src dst label))

(defn- -add-vertex
  [this label]
  (log/info "this:" this)
  (log/info "label:" label)
  (.addVertex this label))

(defn- -backup
  [this ^String path]
  (.backup this path))

(defn- -closed?
  [this]
  (.isClosed this))

(defn- -commit
  [this]
  (.commit (.tx this)))

(defn- -configuration
  [this]
  (.getConfiguration this))

(defn -disconnect
  [this]
  (.close this))

(defn -edges
  [this & args]
  (.edges this args))

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

(defn- -tx
  [this]
  (.tx this))

(defn- -variables
  [this]
  (.asMap (.variables this)))

(defn- -vertices
  ([this]
    (-vertices this []))
  ([this ids]
    (iterator-seq (.vertices this (object-array ids)))))

(def behaviour
  {:add-edge -add-edge
   :add-vertex -add-vertex
   :backup -backup
   :closed? -closed?
   :commit -commit
   :configuration -configuration
   :disconnect -disconnect
   :edges -edges
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
   :show-features -show-features
   :variables -variables
   :vertices -vertices})

(extend StandardJanusGraph
        GraphDBAPI
        behaviour)
