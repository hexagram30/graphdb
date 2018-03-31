(ns hxgm30.graphdb.plugin.backend
  (:require
    [hxgm30.graphdb.plugin.util :as util]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)))

(defn create-component
  [^Keyword backend]
  ((util/get-var backend :component 'create-component)))

(defn get-component-deps
  [^Keyword backend]
  (var-get (util/get-var backend :component 'component-deps)))

(defn create-factory
  [^Keyword backend]
  ((util/get-var backend :factory 'create)))

(defn get-conn
  [^Keyword backend system]
  ((util/get-var backend :component 'get-conn) system))

(defn get-factory
  [^Keyword backend system]
  ((util/get-var backend :component 'get-factory) system))

(defn db-call
  [^Keyword backend system func & args]
  (let [db-call-fn (util/get-var backend :component 'db-call)]
    (log/debugf "Using db-call %s with args %s ..."
                db-call-fn
                [system func args])
    (db-call-fn system func args)))

(defn factory-call
  [^Keyword backend system func & args]
  ((util/get-var backend :component 'factory-call) system func args))
