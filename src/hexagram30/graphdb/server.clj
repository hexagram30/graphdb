(ns hexagram30.graphdb.server
  (:require
    [clojure.java.io :as io]
    [clojusc.twig :as logger]
    [hexagram30.graphdb.config :as config]
    [taoensso.timbre :as log])
  (:import
    (com.orientechnologies.orient.server OServerMain))
  (:gen-class))

(defn set-home!
  [cfg]
  (log/debug "Setting ORIENTDB_HOME ...")
  (let [home-path (get-in cfg [:orientdb :home])
        home (.getAbsolutePath (io/file home-path))]
    (System/setProperty "ORIENTDB_HOME" home)))

(defn start
  [cfg]
  (log/debug "Starting OrientDB embedded database ...")
  (set-home! cfg)
  (let [server (OServerMain/create)
        cfg (config/generate-xml-stream cfg)]
    (.startup server cfg)
    (.activate server)
    server))

(defn stop
  [server]
  (log/debug "Shutting down OrientDB database ...")
  (.shutdown server))

(defn -main
  [& args]
  (let [cfg (config/data)]
    (logger/set-level! (get-in cfg [:logging :nss])
                       (get-in cfg [:logging :level]))
    (start cfg)))
