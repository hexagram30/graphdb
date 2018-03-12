(defproject hexagram30/graphdb "0.1.0-SNAPSHOT"
  :description "A graph database, built on OrientDB, for use by hexagram30 projects"
  :url "https://github.com/hexagram30/graphdb"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojusc/twig "0.3.2"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [org.clojure/clojure "1.8.0"]
    [org.clojure/data.xml "0.0.8"]]
  :plugins [
    [venantius/ultra "0.5.2"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :test {
      :plugins [[lein-ltest "0.3.0"]]}}
  :aliases {
    "compile" ["do"
      ["clean"]
      ["with-profile" "+ubercompile" "compile"]]})
