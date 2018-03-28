(ns hxgm30.graphdb.util
  (:require
    [clojure.walk :as walk]))

(defn keys->strs
  "Recursively transforms all map keys from keywords to strings."
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(name k) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn import-class
  [package-and-class]
  (try (.importClass
        (the-ns *ns*)
        (clojure.lang.RT/classForName (str package-and-class)))
    (catch Exception _
      nil)))

(defn require-ns
  [an-ns]
  (find-ns an-ns))
