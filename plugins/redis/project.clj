(defproject hexagram30/graphdb-redis-plugin "0.1.0-SNAPSHOT"
  :description "A redis backend for the hexagram30 graph database"
  :url "https://github.com/hexagram30/graphdb"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [aysylu/loom "1.0.1"]
    [clojusc/dev-system "0.1.0"]
    [clojusc/trifl "0.2.0"]
    [clojusc/twig "0.3.2"]
    [com.taoensso/carmine "2.18.0"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [org.clojure/clojure "1.8.0"]]
  :jvm-opts ["-Dgraph.backend=redis"]
  :source-paths ["src"]
  :target-path "../../target/%s/"
  :clean-targets ^:replace [])
