(ns hxgm30.graphdb.api.factory
  (:require
    [hxgm30.graphdb.api.impl.tinkerpop2.factory :as tinkerpop2])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory)))

(defprotocol DBFactoryAPI
  (connect [this] [this opts])
  (destroy [this]))

(extend OrientGraphFactory
        DBFactoryAPI
        tinkerpop2/behaviour)

(defn create
  [factory-type spec]
  (case factory-type
    :tinkerpop2 (tinkerpop2/create spec)))
