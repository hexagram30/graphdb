(ns hxgm30.graphdb.plugin.redisgraph.component
  (:require
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.plugin.redisgraph.api.db :as db]
    [hxgm30.graphdb.plugin.redisgraph.api.factory :as factory]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Dependencies   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def component-deps [:config :logging])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Redis Config   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn redis-host
  [system]
  (get-in (config/get-cfg system) [:backend :redisgraph :host]))

(defn redis-port
  [system]
  (get-in (config/get-cfg system) [:backend :redisgraph :port]))

(defn redis-graph-db
  [system]
  (get-in (config/get-cfg system) [:backend :redisgraph :graph :db]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Redis Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-spec
  [system]
  {:host (redis-host system)
   :port (redis-port system)})

(defn get-conn
  [system]
  (get-in system [:backend :conn]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Redis [conn])

(defn start
  [this]
  (log/info "Starting RedisGraph component ...")
  (let [f (factory/create (get-spec this))
        conn (factory/connect f (redis-graph-db this))]
    (log/debug "Started RedisGraph component.")
    (assoc this :conn conn)))

(defn stop
  [this]
  (log/info "Stopping RedisGraph component ...")
  (log/debug "Stopped RedisGraph component.")
  (assoc this :conn nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Redis
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->Redis {}))
