(defproject hexagram30/graphdb "0.1.0-SNAPSHOT"
  :description "A graph database, built on OrientDB, for use by hexagram30 projects"
  :url "https://github.com/hexagram30/graphdb"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojusc/twig "0.3.2"]
    [com.orientechnologies/orientdb-core "2.2.33"]
    [com.orientechnologies/orientdb-server "2.2.33"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [org.clojure/clojure "1.8.0"]
    [org.clojure/data.xml "0.0.8"]]
  :plugins [
    [venantius/ultra "0.5.2"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.2.5"]
        [lein-ancient "0.6.15"]
        [lein-bikeshed "0.5.1"]
        [lein-kibit "0.1.6"]
        [venantius/yagni "0.1.4"]]}
    :test {
      :plugins [[lein-ltest "0.3.0"]]}
      :server {
        :jvm-opts ["-XX:MaxDirectMemorySize=512g"]
        :main hexagram30.graphdb.server}}
  :aliases {
    ;; Dev Aliases
    "ubercompile" ["do"
      ["clean"]
      ["with-profile" "+ubercompile" "compile"]]
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint" ["do"
      ["kibit"]
      ;["eastwood"]
      ]
    "ltest" ["with-profile" "+test" "ltest"]
    "start-db" ["do"
      ["clean"]
      ["with-profile" "+server" "run"]]})
