(ns hxgm30.graphdb.api.impl.redis.util)

(defn parse-node-props
  [props]
  [(:label props) (dissoc props :label)])
