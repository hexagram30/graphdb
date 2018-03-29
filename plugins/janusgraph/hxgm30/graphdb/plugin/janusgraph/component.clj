(ns hxgm30.graphdb.plugin.janusgraph.component
  (:require
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.plugin.janusgraph.api.db :as db]
    [hxgm30.graphdb.plugin.janusgraph.api.factory :as factory]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)
    (clojure.lang Symbol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Dependencies   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def component-deps [:config :logging])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   JanusGraph Config   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn janus-storage-backend
  [system]
  (get-in (config/get-cfg system) [:backend :janusgraph :storage :backend]))

(defn janus-storage-directory
  [system]
  (get-in (config/get-cfg system) [:backend :janusgraph :storage :directory]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   JanusGraph Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-spec
  [system]
  {:storage-backend (janus-storage-backend system)
   :storage-directory (janus-storage-directory system)})

(defn get-conn
  [system]
  (get-in system [:backend :conn]))

(defn get-factory
  [system]
  (get-in system [:backend :factory]))

(defn db-call
  [system ^Symbol func args]
  (apply
    (ns-resolve 'hxgm30.graphdb.plugin.janusgraph.api.db func)
    (concat [(get-conn system)] args)))

(defn factory-call
  [system ^Symbol func args]
  (apply
    (ns-resolve 'hxgm30.graphdb.plugin.janusgraph.api.factory func)
    (concat [(get-factory system)] args)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord JanusGraph [conn factory])

(defn start
  [this]
  (log/info "Starting JanusGraph component ...")
  (let [f (factory/create)
        conn (factory/connect f (get-spec this))]
    (log/debug "Started JanusGraph component.")
    (assoc this :conn conn :factory f)))

(defn stop
  [this]
  (log/info "Stopping JanusGraph component ...")
  (db/disconnect (:conn this))
  (log/debug "Stopped JanusGraph component.")
  (assoc this :conn nil :factory nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend JanusGraph
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->JanusGraph {}))
