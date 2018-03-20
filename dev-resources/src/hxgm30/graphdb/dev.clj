(ns hxgm30.graphdb.dev
  (:require
    [clojure.data.xml :as xml]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl]
    [clojurewerkz.ogre.core :as ogre]
    [clojusc.twig :as logger]
    [hxgm30.graphdb.api.db :as db]
    [hxgm30.graphdb.api.factory :as factory]
    [hxgm30.graphdb.config :as config]
    [trifl.java :refer [show-methods]])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory)))

(logger/set-level! '[hxgm30] :info)

(def refresh #'repl/refresh)

(def test-spec
  {:protocol "remote"
   :path "localhost/test"
   :user "root"
   :password "root"
   :pool {:min-db-instances 1
          :max-db-instances 10}})

(comment
  (def f (factory/create :tinkerpop2 test-spec))
  (def g (factory/connect f))
  ;; If you haven't created any vertices:
  (def cave (db/add-vertex g {:type :room
                              :name "A cave"
                              :description "You are in a dark cave."}))
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
