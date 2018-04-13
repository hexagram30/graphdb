(ns hxgm30.graphdb.plugin.redis.api.util
  (:require
    [clojure.string :as string]))

(defn get-uuid
  [id]
  (last (string/split id #":")))
