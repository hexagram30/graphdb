(ns hxgm30.graphdb.config
  (:require
   [clojure.edn :as edn]
   [clojure.string :as string]
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/graphdb.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (let [cfg (common/read-edn-resource filename)
          backend-cfg (:backend cfg)]
      ;; Only include backend config data for the enabled backend
      (assoc cfg :backend (select-keys
                           backend-cfg
                           [:plugin (:plugin backend-cfg)])))))
