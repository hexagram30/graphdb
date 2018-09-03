(ns hxgm30.graphdb.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.db.plugin.backend :as backend]
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.components.logging :as logging]
    [hxgm30.graphdb.config :as cfg-lib]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cfg
  [data]
  {:config (config/create-component data)})

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(defn backend
  [data]
  (let [backend (get-in data [:backend :plugin])]
    {:backend (component/using
               (backend/create-component backend)
               (backend/get-component-deps backend))}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (let [cfg-data (cfg-lib/data)]
    (component/map->SystemMap
      (merge (cfg cfg-data)
             log))))

(defn initialize-with-backend
  []
  (let [cfg-data (cfg-lib/data)]
    (component/map->SystemMap
      (merge (cfg cfg-data)
             log
             (backend cfg-data)))))

(def init-lookup
  {:basic #'initialize-bare-bones
   :backend #'initialize-with-backend})

(defn init
  ([]
    (init :backend))
  ([mode]
    ((mode init-lookup))))
