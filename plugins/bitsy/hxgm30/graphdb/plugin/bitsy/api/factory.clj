(ns hxgm30.graphdb.plugin.bitsy.api.factory
  (:require
    [clojure.java.io :as io])
  (:import
    (com.lambdazen.bitsy BitsyGraph)
    (com.lambdazen.bitsy.wrapper BitsyAutoReloadingGraph)
    (java.net URI)
    (java.nio.file Paths)))

(load "/hxgm30/graphdb/plugin/protocols/factory")

(defn- create-data-dir!
  [file]
  (when-not (.exists file)
    (->> file
         (.getPath)
         (format "%s/nil")
         (io/make-parents))))

(defn- get-fs-path
  [protocol file]
  (->> file
       (.getAbsolutePath)
       (format "%s:%s" protocol)
       (new URI)
       (Paths/get)))

(defrecord BitsyGraphFactory [
  path
  file
  fs-path])

(defn- -connect
  ([this]
    (-connect this {}))
  ([this opts]
    (let [g (new BitsyGraph (:fs-path this))]
      (if (true? (:autoreload? opts))
        (new BitsyAutoReloadingGraph g)
        g))))

(defn- -destroy
  [this]
  ;; No-op
  )

(def behaviour
  {:connect -connect
   :destroy -destroy})

(extend BitsyGraphFactory
        DBFactoryAPI
        behaviour)

(defn create
  [spec]
  (let [db-path (:path spec)
        file (io/file db-path)]
    (create-data-dir! file)
    (map->BitsyGraphFactory
      {:path db-path
       :file file
       :fs-path (get-fs-path (:protocol spec) file)})))
