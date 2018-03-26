(ns hxgm30.graphdb.plugin.redis.api.util)

(defn parse-node-props
  [props]
  [(:label props) (dissoc props :label)])
