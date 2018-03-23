(ns hxgm30.graphdb.api.factory
  (:require
    [hxgm30.graphdb.api.impl.bitsy.factory :as bitsy]
    [hxgm30.graphdb.api.impl.orientdb.factory :as orientdb]
    [hxgm30.graphdb.api.impl.redis.factory :as redis])
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory)
    (hxgm30.graphdb.api.impl.bitsy.factory BitsyGraphFactory)
    (hxgm30.graphdb.api.impl.redis.factory RedisGraphFactory)))

(defprotocol DBFactoryAPI
  (connect [this] [this opts])
  (destroy [this]))

(extend OrientGraphFactory
        DBFactoryAPI
        orientdb/behaviour)

(extend BitsyGraphFactory
        DBFactoryAPI
        bitsy/behaviour)

(extend RedisGraphFactory
        DBFactoryAPI
        redis/behaviour)

(defn create
  [factory-type spec]
  (case factory-type
    :orientdb (orientdb/create spec)
    :bitsy (bitsy/create spec)
    :redis (redis/create spec)))
