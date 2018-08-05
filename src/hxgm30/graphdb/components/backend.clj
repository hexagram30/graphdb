(ns hxgm30.graphdb.components.backend
  "The starting and stopping of the backend component is actually done via
  backend-specific components which are:
  * configured via `project.clj` and `resources/hexagram30-config/graphdb.edn`
  * loaded via `hxgm30.graphdb.plugin.backend`

  Being backend-specific, those start and stop functions vary based on the
  database defined and used in the pluging. However, many operations are the
  same across all plugins.

  This namespace supports the generic operations of the backends, letting the
  plugins handle actual component lifecycle operations."
  (:require
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.plugin.util :as util]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn backend
  [system]
  (config/backend-plugin system))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Backend Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn db-conn
  ([system]
    (db-conn system (backend system)))
  ([system ^Keyword backend]
    ((util/get-var backend :component 'get-conn) system)))

(defn factory
  ([system]
    (factory system (backend system)))
  ([system ^Keyword backend]
    ((util/get-var backend :component 'get-factory) system)))

(defn db-call
  ([system func]
    (db-call system func []))
  ([system func args]
    (db-call system (backend system) func args))
  ([system ^Keyword backend func args]
    (let [db-call-fn (util/get-var backend :component 'db-call)]
      (log/debugf "Using db-call %s with args %s ..."
                  db-call-fn
                  [system func args])
      (db-call-fn system func args))))

(defn factory-call
  ([system func]
    (factory-call system func []))
  ([system func args]
    (factory-call system (backend system) func args))
  ([system ^Keyword backend func args]
    ((util/get-var backend :component 'factory-call) system func args)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Not applicable for this component; see the component for the plugin in
;; question for more details.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Not applicable for this component; see the component for the plugin in
;; question for more details.
