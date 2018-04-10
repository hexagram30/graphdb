(ns hxgm30.graphdb.dev
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string]
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
;;;   Data Support   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn status
  []
  (system-api/get-status (:state (mgr-arg))))

(defn system
  []
  (system-api/get-system (:state (mgr-arg))))

(defn backend
  []
  (config/backend-plugin (system)))

(defn conn
  []
  (backend/get-conn (backend) (system)))

(defn factory
  []
  (backend/get-factory (backend) (system)))

(defn load-backend-specific-dev
  []
  (condp = (backend)
    :redis (load "/hxgm30/graphdb/plugin/redis/dev")
    :skip-load))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   State Management   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn startup
  []
  (alter-var-root #'*mgr* (constantly (system-api/create-state-manager)))
  (system-api/set-system-ns (:state *mgr*) "hxgm30.graphdb.components.core")
  (system-api/startup *mgr*)
  (load-backend-specific-dev)
  (status))

(defn shutdown
  []
  (when *mgr*
    (let [result (system-api/shutdown (mgr-arg))]
      (alter-var-root #'*mgr* (constantly nil))
      result)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Reloading Management   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn reset
  []
  (shutdown)
  (repl/refresh :after 'hxgm30.graphdb.dev/startup))

(def refresh #'repl/refresh)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Data Macros and Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro defn-factory
  [wrapper-name & rest]
  `(defn ~wrapper-name
    [~@rest]
    (backend/factory-call (backend) (system) '~wrapper-name)))

(defn-factory dbs)

(defmacro defn-db
  [wrapper-name & rest]
  (let [args? (and (coll? rest)
                   (seq rest))]
  `(defn ~wrapper-name
    [~@rest]
    ~(if args?
      `(backend/db-call (backend) (system) '~wrapper-name ~@rest)
      `(backend/db-call (backend) (system) '~wrapper-name)))))

(defmacro db-call
  [func & rest]
  (let [args? (and (coll? rest)
                   (seq rest))]
  (if args?
    `(backend/db-call (backend) (system) '~func ~@rest)
    `(backend/db-call (backend) (system) '~func))))

(defn add-edge
  ([src dst]
    (db-call add-edge src dst))
  ([src dst attrs]
    (db-call add-edge src dst attrs))
  ([src dst attrs label attrs]
    (db-call add-edge src dst attrs label attrs)))

(defn add-vertex
  ([]
    (db-call add-vertex))
  ([attrs]
    (db-call add-vertex attrs)))

(defn-db backup)
(defn-db closed?)
(defn-db commit)
(defn-db configuration)

(defn create-index
  ([data-type]
    (db-call create-index data-type))
  ([data-type id]
    (db-call create-index data-type id)))

(defn-db dump)
(defn-db edges)
(defn-db features)
(defn-db get-edge id)
(defn-db get-edges)
(defn-db get-relations)
(defn-db get-vertex id)
(defn-db get-vertex-relations id)
(defn-db get-vertices)
(defn-db get-vertices-relations ids)
(defn-db find-relation-ids)
(defn-db find-relations vertex-id)
(defn-db find-vertex-ids)
(defn-db graph-name)
(defn-db open?)
(defn-db relations)
(defn-db remove-edge id)
(defn-db remove-edges)
(defn-db remove-vertex id)
(defn-db remove-relation rid vid)
(defn-db remove-relations vid)
(defn-db variables)

(defn vertices
  ([]
    (db-call vertices))
  ([ids]
    (db-call vertices ids)))

(comment

  ;;--  Exploring the new Redis backend API  ------------------------------;;

  (add-vertex {:label :node1})
  (add-vertex {:label :node2})
  (add-vertex {:label :node3})
  (add-vertex {:label :node4})
  (add-vertex {:label :node5})

  (def vs (get-vertices))
  vs
  [{:attrs {"label" "node2"} :id "node:ba00e4e6-a9b3-408a-9c4f-c356cf3e7534"}
   {:attrs {"label" "node4"} :id "node:964fc62b-1942-44ef-bbb7-f1cdbfdb7e66"}
   {:attrs {"label" "node3"} :id "node:6173a57e-7a5d-4aa3-ad3c-ec2497b2552b"}
   {:attrs {"label" "node1"} :id "node:30a3d608-6f11-465e-8fe5-0d0de526ed68"}
   {:attrs {"label" "node5"} :id "node:3f701956-eb4f-4c1a-a7c0-7bc5cb6b6361"}]


  (add-edge "node:30a3d608-6f11-465e-8fe5-0d0de526ed68"
            "node:ba00e4e6-a9b3-408a-9c4f-c356cf3e7534")
  (add-edge "node:ba00e4e6-a9b3-408a-9c4f-c356cf3e7534"
            "node:6173a57e-7a5d-4aa3-ad3c-ec2497b2552b")
  (add-edge "node:ba00e4e6-a9b3-408a-9c4f-c356cf3e7534"
            "node:964fc62b-1942-44ef-bbb7-f1cdbfdb7e66")
  (add-edge "node:ba00e4e6-a9b3-408a-9c4f-c356cf3e7534"
            "node:3f701956-eb4f-4c1a-a7c0-7bc5cb6b6361")
  (def rs (get-vertices-relations vs))
  rs

  ;;--  Combining loom and the Redis API  ---------------------------------;;

  (def g (queries/graph vs rs (vertices)))
  g
  (loom-io/view g)

  (add-vertex {:type :room :name "A cave" :description "You are in a dark cave."})
  )



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
