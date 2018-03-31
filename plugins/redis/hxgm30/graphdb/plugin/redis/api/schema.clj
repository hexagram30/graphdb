(ns hxgm30.graphdb.plugin.redis.api.schema
  "Schema setup for graph data.

  Adding a node:
    HMSET node:UUID KEY1 VAL1 [KEY2 VAL2 [...]]

  Adding an edge:
    HMSET edge:UUID KEY1 VAL1 [KEY2 VAL2 [...]]

  Adding a relation:
    RPUSH adjc:UUID [UUID1 [UUID2 [...]]

    ")

(def edge-tmpl "edge:%s")
(def relation-tmpl "adjc:%s")
(def vertex-tmpl "node:%s")

(defn edge
  [id]
  (format edge-tmpl id))

(defn relation
  [id]
  (format relation-tmpl id))

(defn vertex
  [id]
  (format vertex-tmpl id))
