(ns hxgm30.graphdb.util
  (:require
    [clojure.walk :as walk]))

(defn keys->strs
  "Recursively transforms all map keys from keywords to strings."
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(name k) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn tuple?
  [data]
  (and (= 2 (count data))
       (not (coll? (first data)))))

(defn tuple->map
  [tuple]
  (into {} [(vec tuple)]))

(defn tuples->map
  [tuples]
  (into {} (vec (map vec tuples))))

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

(defn list->map
  "Given a flat list of pairs, convert them to a map."
  [items]
  (->> items
       (partition 2)
       (map (fn [[k v]] [(keyword k) v]))
       vec
       (into {})))
