(ns hxgm30.graphdb.config
  (:require
   [clojure.data.xml :as xml]
   [clojure.edn :as edn]
   [clojure.string :as string]
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/graphdb.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (common/read-edn-resource filename)))

(defn user-creds
  [parsed-edn]
  (let [file (get-in parsed-edn [:user :password-file])]
    (->> (System/getenv "HOME")
         (string/replace file #"^~")
         (slurp)
         (edn/read-string))))

(defn user-password
  [parsed-edn]
  (-> parsed-edn
      user-creds
      (get-in [:users :root :password])))

(defn db-config
  ([]
    (db-config (data)))
  ([parsed-edn]
    (get-in parsed-edn [:orientdb :db])))

(defn http-config
  ([]
    (http-config (data)))
  ([parsed-edn]
    (get-in parsed-edn [:orientdb :httpd])))

(defn httpd-edn->orientdb-xml
  [parsed-edn]
  )

(defn db-edn->orientdb-xml
  [parsed-edn]
  )

(defn generate-xml-stream
  ([]
    (generate-xml-stream (data)))
  ([parsed-edn]
    (let [db (db-config parsed-edn)
          pw (user-password parsed-edn)]
      (-> db
          ;(assoc-in [2 1 1 :password] pw)
          (xml/sexp-as-element)
          (xml/emit-str)))))
