(ns hxgm30.graphdb.plugin.util
  (:import
    (clojure.lang Keyword)
    (clojure.lang Symbol)))

(def base-ns "hxgm30.graphdb.plugin")

(defn get-ns
  [^Keyword backend ns-suffix]
  (symbol (format "%s.%s.%s" base-ns (name backend) ns-suffix)))

(defn get-db-ns
  [^Keyword backend]
  (get-ns backend "api.db"))

(defn get-factory-ns
  [^Keyword backend]
  (get-ns backend "api.factory"))

(defn get-component-ns
  [^Keyword backend]
  (get-ns backend "component"))

(def ns-lookup
  {:db get-db-ns
   :factory get-factory-ns
   :component get-component-ns})

(defn get-var
  [^Keyword backend ^Keyword ns-type^Symbol sym]
  (let [namesp ((ns-type ns-lookup) backend)]
    (require namesp)
    (ns-resolve namesp sym)))
