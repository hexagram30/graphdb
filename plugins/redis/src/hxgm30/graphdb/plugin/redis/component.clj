(ns hxgm30.graphdb.plugin.redis.component
  (:require
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.plugin.redis.api.db :as db]
    [hxgm30.graphdb.plugin.redis.api.factory :as factory]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Symbol)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Dependencies   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def component-deps [:config :logging])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Redis Config   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn redis-host
  [system]
  (get-in (config/get-cfg system) [:backend :redis :host]))

(defn redis-port
  [system]
  (get-in (config/get-cfg system) [:backend :redis :port]))

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

(defn get-factory
  [system]
  (get-in system [:backend :factory]))

(defn db-call
  ([system ^Symbol func]
    (db-call system func []))
  ([system ^Symbol func args]
    (log/trace "Component preparing to call into plugin db API ...")
    (log/tracef "Got system, func, args: %s, %s, %s" system func args)
    (let [resolved-func (ns-resolve 'hxgm30.graphdb.plugin.redis.api.db func)]
      (log/trace "Got resolved function:" resolved-func)
      (when (nil? resolved-func)
        (log/error (str "Couldn't find function in given namespace; "
                        "has it been added to the protocol/behaviour?")))
      (apply
        resolved-func
        (concat [(get-conn system)] args)))))

(defn db*
  [system ^Symbol func & args]
  (db-call system func args))

(defn factory-call
  ([system ^Symbol func]
    (factory-call system func []))
  ([system ^Symbol func args]
    (log/trace "Component preparing to call into plugin factory API ...")
    (apply
      (ns-resolve 'hxgm30.graphdb.plugin.redis.api.factory func)
      (concat [(get-factory system)] args))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Redis [conn])

(defn start
  [this]
  (log/info "Starting Redis component ...")
  (let [f (factory/create (get-spec this))
        conn (factory/connect f)]
    (log/debug "Started Redis component.")
    (assoc this :conn conn)))

(defn stop
  [this]
  (log/info "Stopping Redis component ...")
  (log/debug "Stopped Redis component.")
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
