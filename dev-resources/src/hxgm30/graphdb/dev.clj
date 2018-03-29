(ns hxgm30.graphdb.dev
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.dev.system.core :as system-api]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.graphdb.components.config :as config]
    [hxgm30.graphdb.components.core]
    [hxgm30.graphdb.plugin.backend :as backend]
    [hxgm30.graphdb.plugin.util :as plugin-util]
    [hxgm30.graphdb.util :as util]
    [trifl.java :refer [show-methods]])
  (:import
    (java.net URI)
    (java.nio.file Paths)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Initial Setup & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(logger/set-level! '[hxgm30] :debug)

(def ^:dynamic *mgr* nil)

(defn banner
  []
  (println (slurp (io/resource "text/banner.txt")))
  :ok)

(defn mgr-arg
  []
  (if *mgr*
    *mgr*
    (throw (new Exception
                (str "A state manager is not defined; "
                     "have you run (startup)?")))))

(defn system-arg
  []
  (if-let [state (:state *mgr*)]
    (system-api/get-system state)
    (throw (new Exception
                (str "System data structure is not defined; "
                     "have you run (startup)?")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   State Management   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn startup
  []
  (alter-var-root #'*mgr* (constantly (system-api/create-state-manager)))
  (system-api/set-system-ns (:state *mgr*) "hxgm30.graphdb.components.core")
  (system-api/startup *mgr*))

(defn shutdown
  []
  (when *mgr*
    (let [result (system-api/shutdown (mgr-arg))]
      (alter-var-root #'*mgr* (constantly nil))
      result)))

(defn system
  []
  (system-api/get-system (:state (mgr-arg))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Reloading Management   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn reset
  []
  (shutdown)
  (repl/refresh :after 'hxgm30.graphdb.dev/startup))

(def refresh #'repl/refresh)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Data Support   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn backend
  []
  (config/backend-plugin (system)))

(defn conn
  []
  (backend/get-conn (backend) (system)))

(defn factory
  []
  (backend/get-factory (backend) (system)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Data   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro defn-factory
  [wrapper-name & rest]
  `(defn ~wrapper-name
    [~@rest]
    (backend/factory-call (backend) (system) '~wrapper-name)))

(defn-factory dbs)

(defmacro defn-db
  [wrapper-name & rest]
  `(defn ~wrapper-name
    [~@rest]
    (backend/db-call (backend) (system) '~wrapper-name)))

(defn-db configuration)
(defn-db features)
(defn-db graph-name)
(defn-db open?)
(defn-db closed?)

(comment
  (startup)
  (def f (factory/create :redis redis-spec))
  (def g (factory/connect f :game))
  ;; If you haven't created any vertices:
  (def cave (db/add-vertex g {:type :room :name "A cave" :description "You are in a dark cave."}))
  (def tunnel (db/add-vertex g {:type :room
                                :name "A tunnel"
                                :description "You are in a long, dark tunnel."}))
  (db/commit g)
  ;; If you have created the vertices:

  ;; If you haven't created any edges:
  (def cave->tunnel (db/add-edge g cave, tunnel "has exit"))
  (def tunnel->cave (db/add-edge g tunnel, cave "has exit"))
  (db/commit g)
  ;; If you have created the edges:

  ;; Now visit http://localhost:2480, login, and then take a look at the
  ;; vertices and edges ...
  (db/disconnect g)
  (factory/destroy f))

