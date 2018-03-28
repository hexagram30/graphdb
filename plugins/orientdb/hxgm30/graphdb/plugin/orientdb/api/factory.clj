(ns hxgm30.graphdb.plugin.orientdb.api.factory
  "This is the implementation that's intended to be used with OrientDB v 2.2.x.

  Resources for this implementation:
  * https://orientdb.com/docs/2.2.x/Java-API.html
  * https://orientdb.com/docs/2.2.x/Tutorial-Java.html
  * https://orientdb.com/docs/2.2.x/Graph-Factory.html
  * https://orientdb.com/docs/2.2.x/Graph-Database-Tinkerpop.html
  * https://orientdb.com/javadoc/2.2.x/index.html?com/tinkerpop/blueprints/impls/orient/OrientGraphFactory.html"
  (:import
    (com.tinkerpop.blueprints.impls.orient OrientGraphFactory))
  (:refer-clojure :exclude [flush]))

(load "/hxgm30/graphdb/plugin/protocols/factory")

(defn- -connect
  ([this]
    (-connect this {}))
  ([this opts]
    (if (= false (:transactional? opts))
      (.getNoTx this)
      (.getTx this))))

(defn- -destroy
  [this]
  (.close this))

(def behaviour
  {:connect -connect
   :destroy -destroy})

(extend OrientGraphFactory
        DBFactoryAPI
        behaviour)

(defn create
  [spec]
  (let [factory (new OrientGraphFactory (format "%s:%s"
                                                (:protocol spec)
                                                (:path spec)
                                                (:user spec)
                                                (:password spec)))]
    (.setupPool factory
                (get-in spec [:pool :min-db-instances])
                (get-in spec [:pool :max-db-instances]))))
