(ns hxgm30.graphdb.api.factory
  (:require
    [hxgm30.graphdb.api.impl.bitsy.factory :as bitsy]
    [hxgm30.graphdb.api.impl.orientdb.factory :as orientdb])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory)
    (hxgm30.graphdb.api.impl.bitsy.factory BitsyGraphFactory)))

(defprotocol DBFactoryAPI
  (connect [this] [this opts])
  (destroy [this]))

(extend OrientGraphFactory
        DBFactoryAPI
        orientdb/behaviour)

(extend BitsyGraphFactory
        DBFactoryAPI
        bitsy/behaviour)

(defn create
  [factory-type spec]
  (case factory-type
    :orientdb (orientdb/create spec)
    :bitsy (bitsy/create spec)))
