(ns hxgm30.graphdb.config
  (:require
   [clojure.edn :as edn]
   [clojure.string :as string]
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/graphdb.edn")

(defn get-backend-type
  [cfg-data]
  (or (keyword (System/getProperty "graph.backend"))
      (get-in cfg-data [:backend :plugin])))

(defn get-backend-subtype
  [cfg-data]
  (or (keyword (System/getProperty "graph.backend.subtype"))
      (get-in cfg-data [:backend :subtype])))

(defn data
  ([]
    (data config-file))
  ([filename]
    (let [cfg (common/read-edn-resource filename)]
      (-> cfg
          ;; If something was defined using -D on the CLI, pull it in
          (assoc-in [:backend :plugin] (get-backend-type cfg))
          (assoc-in [:backend :subtype] (get-backend-subtype cfg))))))
