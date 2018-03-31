(ns hxgm30.graphdb.plugin.bitsy.component
  (:require
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.plugin.bitsy.api.db :as db]
    [hxgm30.graphdb.plugin.bitsy.api.factory :as factory]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Symbol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Dependencies   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def component-deps [:config :logging])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Bitsy Config   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bitsy-protocol
  [system]
  (get-in (config/get-cfg system) [:backend :bitsy :protocol]))

(defn bitsy-path
  [system]
  (get-in (config/get-cfg system) [:backend :bitsy :path]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Bitsy Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-spec
  [system]
  {:protocol (bitsy-protocol this)
   :path (bitsy-path this)})

(defn get-conn
  [system]
  (get-in system [:backend :conn]))

(defn get-factory
  [system]
  (get-in system [:backend :factory]))

(defn db-call
  [system ^Symbol func args]
  (apply
    (ns-resolve 'hxgm30.graphdb.plugin.bitsy.api.db func)
    (concat [(get-conn system)] args)))

(defn factory-call
  [system ^Symbol func args]
  (apply
    (ns-resolve 'hxgm30.graphdb.plugin.bitsy.api.factory func)
    (concat [(get-factory system)] args)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Bitsy [conn])

(defn start
  [this]
  (log/info "Starting Bitsy component ...")
  (let [f (factory/create (get-spec this))
        conn (factory/connect f)]
    (log/debug "Started Bitsy component.")
    (assoc this :conn conn)))

(defn stop
  [this]
  (log/info "Stopping Bitsy component ...")
  (log/debug "Stopped Bitsy component.")
  (assoc this :conn nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Bitsy
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->Bitsy {}))
