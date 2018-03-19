(ns hxgm30.graphdb.server
  (:require
    [clojure.java.io :as io]
    [clojusc.twig :as logger]
    [hxgm30.graphdb.config :as config]
    [taoensso.timbre :as log])
  (:import
    (com.orientechnologies.orient.server OServerMain))
  (:gen-class))

(defn set-pw!
  [cfg-edn]
  (log/debug "Setting ORIENTDB_ROOT_PASSWORD ...")
  (->> cfg-edn
       config/user-password
       (System/setProperty "ORIENTDB_ROOT_PASSWORD")))

(defn set-home!
  [cfg-edn]
  (log/debug "Setting ORIENTDB_HOME ...")
  (let [home-path (get-in cfg-edn [:orientdb :home])
        home (.getAbsolutePath (io/file home-path))]
    (System/setProperty "ORIENTDB_HOME" home)))

(defn start
  [cfg-edn]
  (log/debug "Starting OrientDB embedded database ...")
  (set-home! cfg-edn)
  (set-pw! cfg-edn)
  (let [server (OServerMain/create)
        cfg (config/generate-xml-stream cfg-edn)]
    (log/debug "server:" server)
    (log/debug "cfg:" cfg)
    (.startup server cfg)
    (.activate server)
    server))

(defn stop
  [server]
  (log/debug "Shutting down OrientDB database ...")
  (.shutdown server))

(defn -main
  [& args]
  (let [cfg-edn (config/data)]
    (logger/set-level! (get-in cfg-edn [:logging :nss])
                       (get-in cfg-edn [:logging :level]))
    (start cfg-edn)))
