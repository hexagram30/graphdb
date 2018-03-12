(ns hexagram30.graphdb.config
  (:require
   [clojure.data.xml :as xml]
   [hexagram30.common.file :as common]))

(def config-file "hexagram30-config/graphdb.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (common/read-edn-resource filename)))

(defn httpd-edn->orientdb-xml
  [parsed-edn]
  )

(defn db-edn->orientdb-xml
  [parsed-edn]
  )

(defn generate-xml-stream
  [parsed-edn]
  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><orient-server></orient-server>")
