(ns hxgm30.graphdb.plugin.redis.api.queries
  (:require
    [loom.alg :as alg]
    [loom.alg-generic :as alg-generic]
    [loom.attr :as attr]
    [loom.flow :as flow]
    [loom.graph :as graph]))

(defn adjacency-map
  [vertex-ids relations]
  (zipmap vertex-ids relations))

(defn graph
  ([vertex-ids relations]
    (graph/graph (adjacency-map vertex-ids relations)))
  ([vertex-ids relations attrs]
    (let [g (graph vertex-ids relations)]
      (assoc g :attrs attrs))))
