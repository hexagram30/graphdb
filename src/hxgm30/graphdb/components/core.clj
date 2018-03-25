(ns hxgm30.graphdb.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.components.logging :as logging]
    [hxgm30.graphdb.components.redis :as redis]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cfg
  {:config (config/create-component)})

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(def redis
  {:redis (component/using
           (redis/create-component)
           [:config :logging])})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (component/map->SystemMap
    (merge cfg
           log)))

(defn initialize-with-db
  []
  (component/map->SystemMap
    (merge cfg
           log
           redis)))

(def init-lookup
  {:basic #'initialize-bare-bones
   :db #'initialize-with-db})

(defn init
  ([]
    (init :db))
  ([mode]
    ((mode init-lookup))))
