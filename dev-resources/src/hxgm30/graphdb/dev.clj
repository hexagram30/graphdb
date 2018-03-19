(ns hxgm30.graphdb.dev
  (:require
    [clj-odbp.core :as odbp]
    [clojure.data.xml :as xml]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl]
    [clojurewerkz.ogre.core :as ogre]
    [clojusc.twig :as logger]
    [hxgm30.graphdb.config :as config]
    [hxgm30.graphdb.server :as server]))

(logger/set-level! ['hxgm30] :info)

(def refresh #'repl/refresh)
