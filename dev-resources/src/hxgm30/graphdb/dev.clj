(ns hxgm30.graphdb.dev
  (:require
    [clojure.data.xml :as xml]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl]
    [clojurewerkz.ogre.core :as ogre]
    [clojusc.twig :as logger]
    [hxgm30.graphdb.config :as config]
    [trifl.java :refer [show-methods]])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory)))

(logger/set-level! ['hxgm30] :info)

(def refresh #'repl/refresh)

(def test-spec
  {:protocol "remote"
   :path "localhost/test"
   :user "root"
   :password "root"
   :pool {:min-db-instances 1
          :max-db-instances 10}})

(defn create-factory
  [spec]
  (let [factory (new OrientGraphFactory (format "%s:%s"
                                                (:protocol spec)
                                                (:path spec)
                                                (:user spec)
                                                (:password spec)))]
    (.setupPool factory
                (get-in spec [:pool :min-db-instances])
                (get-in spec [:pool :max-db-instances]))))

(defn destroy-factory
  [factory]
  (.close factory))

(defn connect
  ([factory]
    (connect factory {}))
  ([factory opts]
    (if (not (:transactional? opts))
      (.getNoTx factory)
      (.getTx factory))))

(defn disconnect
  [conn]
  (.shutdown conn))

(defn add-vertex
  [graph props]
  (.addVertex graph props))

(comment
  (def f (create-factory test-spec))
  (def g (connect f))
  (add-vertex g {:type :room :name "A cave" :description "You are in a dark cave."})
  ;; Now visit http://localhost:2480/studio/index.htm and take a look at the nodes
  (disconnect g)
  (destroy-factory f))
