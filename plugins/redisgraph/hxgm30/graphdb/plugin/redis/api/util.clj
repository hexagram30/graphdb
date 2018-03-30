(ns hxgm30.graphdb.plugin.redisgraph.api.util)

(defn parse-node-props
  [props]
  [(:label props) (dissoc props :label)])
