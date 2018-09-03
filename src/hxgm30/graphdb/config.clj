(ns hxgm30.graphdb.config
  (:require
   [clojure.edn :as edn]
   [clojure.string :as string]
   [hxgm30.common.file :as common]
   [hxgm30.common.util :as util]))

(def config-file "hexagram30-config/graphdb.edn")
(def plugin-config-file "hexagram30-config/db.edn")

(defn get-backend-type
  [cfg-data plugin-cfg-data]
  (or (keyword (or (System/getProperty "graphdb.backend")
                   (System/getProperty "db.backend")))
      (get-in cfg-data [:backend :plugin])
      (get-in plugin-cfg-data [:backend :plugin])))

(defn get-backend-subtype
  [cfg-data plugin-cfg-data]
  (or (keyword (or (System/getProperty "graphdb.backend.subtype")
                   (System/getProperty "db.backend.subtype")))
      (get-in cfg-data [:backend :subtype])
      (get-in plugin-cfg-data [:backend :subtype])))

(defn data
  ([]
    (data config-file))
  ([filename]
    (let [plugin-cfg (common/read-edn-resource plugin-config-file)
          plugin-backend-cfg (:backend plugin-cfg)
          cfg (common/read-edn-resource filename)
          subtype (get-backend-subtype cfg plugin-cfg)]
      (util/deep-merge
        (-> plugin-cfg
            (assoc :backend (select-keys plugin-backend-cfg
                                         [:plugin subtype])))
        (-> cfg
            ;; If something was defined using -D on the CLI, pull it in
            (assoc-in [:backend :plugin] (get-backend-type cfg plugin-cfg))
            (assoc-in [:backend :subtype] subtype))))))
