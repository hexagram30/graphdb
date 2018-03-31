(ns hxgm30.graphdb.plugin.orientdb.component
  (:require
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.plugin.orientdb.api.db :as db]
    [hxgm30.graphdb.plugin.orientdb.api.factory :as factory]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Symbol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Dependencies   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def component-deps [:config :logging])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   OrientDB Config   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn orientdb-protocol
  [system]
  (get-in (config/get-cfg system) [:backend :orientdb :protocol]))

(defn orientdb-path
  [system]
  (get-in (config/get-cfg system) [:backend :orientdb :path]))

(defn orientdb-user
  [system]
  (get-in (config/get-cfg system) [:backend :orientdb :user]))

(defn orientdb-password
  [system]
  (get-in (config/get-cfg system) [:backend :orientdb :password]))

(defn orientdb-pool
  [system]
  (get-in (config/get-cfg system) [:backend :orientdb :pool]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   OrientDB Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-spec
  [system]
  {:protocol (orientdb-protocol system)
   :path (orientdb-path system)
   :user (orientdb-user system)
   :password (orientdb-password system)
   :pool (orientdb-pool system)})

(defn get-conn
  [system]
  (get-in system [:backend :conn]))

(defn get-factory
  [system]
  (get-in system [:backend :factory]))

(defn db-call
  [system ^Symbol func args]
  (apply
    (ns-resolve 'hxgm30.graphdb.plugin.orientdb.api.db func)
    (concat [(get-conn system)] args)))

(defn factory-call
  [system ^Symbol func args]
  (apply
    (ns-resolve 'hxgm30.graphdb.plugin.orientdb.api.factory func)
    (concat [(get-factory system)] args)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord OrientDB [conn])

(defn start
  [this]
  (log/info "Starting OrientDB component ...")
  (let [f (factory/create (get-spec this))
        conn (factory/connect f)]
    (log/debug "Started OrientDB component.")
    (assoc this :conn conn)))

(defn stop
  [this]
  (log/info "Stopping OrientDB component ...")
  (log/debug "Stopped OrientDB component.")
  (assoc this :conn nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend OrientDB
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->OrientDB {}))
