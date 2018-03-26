(ns hxgm30.graphdb.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.components.logging :as logging]
    [hxgm30.graphdb.plugin.backend :as backend]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cfg
  {:config (config/create-component)})

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(def backend
  {:backend (component/using
             (backend/create-component)
             (backend/get-component-deps))})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (component/map->SystemMap
    (merge cfg
           log)))

(defn initialize-with-backend
  []
  (component/map->SystemMap
    (merge cfg
           log
           backend)))

(def init-lookup
  {:basic #'initialize-bare-bones
   :backend #'initialize-with-backend})

(defn init
  ([]
    (init :backend))
  ([mode]
    ((mode init-lookup))))
