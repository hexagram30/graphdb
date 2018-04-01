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
(defn-db remove-relation rid vid)
(defn-db remove-relations vid)
(defn-db variables)

(defn vertices
  ([]
    (db-call vertices))
  ([ids]
    (db-call vertices ids)))

(comment

  ;;--  Exploring the loom API  -------------------------------------------;;

  (def g (graph/graph [1 2] [2 3] {3 [4] 5 [6 7]} 7 8 9))
  (graph/nodes g)
  (graph/edges g)
  (loom-io/view g)
  (graph/successors g 3)
  (alg/bf-path g 1 4)

  (def g2 (graph/add-nodes g "foobar" {:name "baz"} [1 2 3]))
  (loom-io/view g2)
  (def g3 (graph/add-edges g2 [10 11] ["foobar" {:name "baz"}]))
  (loom-io/view g3)

  (def attr-graph
    (-> g
        (attr/add-attr 1 :label "node 1")
        (attr/add-attr 4 :label "node 4")
        (attr/add-attr-to-nodes :parity "even" [2 4])
        (attr/add-attr-to-edges :label "edge from node 5" [[5 6] [5 7]])))
  (loom-io/view attr-graph)

  ;#loom.graph.BasicEditableGraph
  {:adj {1 #{2} 2 #{1 3} 3 #{2 4} 4 #{3} 5 #{6 7} 6 #{5} 7 #{5}}
   :attrs {1 {:label "node 1"}
           2 {:parity "even"}
           4 {:label "node 4" :parity "even"}
           5 {:loom.attr/edge-attrs {6 {:label "edge from node 5"}
                                     7 {:label "edge from node 5"}}}
           6 {:loom.attr/edge-attrs {5 {:label "edge from node 5"}}}
           7 {:loom.attr/edge-attrs {5 {:label "edge from node 5"}}}}
   :nodeset #{1 2 3 4 5 6 7 8 9}}

  ;;--  Exploring the new Redis backend API  ------------------------------;;

  {:id "node:66bbc110-a28b-4b97-86b3-ca97517e28ec" :result "OK"}
  {:id "node:981f100e-9ef6-4843-b898-ac95e5843794" :result "OK"}
  {:id "node:1293bc7d-6725-4660-9e48-7792b1184968" :result "OK"}

  {:id "edge:c3051612-dd5f-47af-b408-f332aef57222" :result "OK"}
  {:id "edge:6fbf5415-0d2d-4fba-a22e-b77e250e22ec" :result "OK"}
  {:id "edge:8426fea9-5385-4235-be08-6022011e0e41" :result "OK"}

  (add-edge "node:66bbc110-a28b-4b97-86b3-ca97517e28ec" "node:981f100e-9ef6-4843-b898-ac95e5843794")
  (add-edge "node:981f100e-9ef6-4843-b898-ac95e5843794" "node:1293bc7d-6725-4660-9e48-7792b1184968")
  (add-edge "node:1293bc7d-6725-4660-9e48-7792b1184968" "node:66bbc110-a28b-4b97-86b3-ca97517e28ec")

  ["adjc:node:66bbc110-a28b-4b97-86b3-ca97517e28ec"
   "adjc:node:1293bc7d-6725-4660-9e48-7792b1184968"
   "adjc:node:981f100e-9ef6-4843-b898-ac95e5843794"]

  (def vs (get-vertices))
  vs
  (def rs (get-vertices-relations vs))
  rs

  ;;--  Combining loom and the Redis API  ---------------------------------;;

  (def g (queries/graph vs rs))
  g
  (loom-io/view g)

  (def as {"node:66bbc110-a28b-4b97-86b3-ca97517e28ec" {:label "node 1"}
           "node:981f100e-9ef6-4843-b898-ac95e5843794" {:label "node 2"}
           "node:1293bc7d-6725-4660-9e48-7792b1184968" {:label "node 3"}})
  (def g (queries/graph vs rs as))
  g
  (loom-io/view g)
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
