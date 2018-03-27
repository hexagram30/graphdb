;;;; Note that this file is intended to be loaded by plugins supporting the
;;;; TinkerPop2 API.

(defn add-edge
  [this src dst label]
  (.addEdge this nil src dst label))

(defn add-vertex
  [this props]
  (.addVertex this (util/keys->strs props)))

(defn commit
  [this]
  (.commit this))

(defn disconnect
  [this]
  (.shutdown this))

(defn get-edge
  [this id]
  (.getEdge this id))

(defn get-edges
  [this]
  (into [] (.getEdges this)))

(defn get-vertex
  [this id]
  (.getVertex this id))

(defn get-vertices
  [this]
  (into [] (.getVertices this)))

(defn remove-edge
  [this edge]
  (.removeEdge this edge))

(defn remove-vertex
  [this vertex]
  (.removeVertex this vertex))

(defn rollback
  [this]
  (.rollback this))

(def tinkerpop2-behaviour
  {:add-edge add-edge
   :add-vertex add-vertex
   :commit commit
   :disconnect disconnect
   :get-edge get-edge
   :get-edges get-edges
   :get-vertex get-vertex
   :get-vertices get-vertices
   :remove-edge remove-edge
   :remove-vertex remove-vertex
   :rollback rollback})
