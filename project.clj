(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      (slurp "resources/text/loading.txt"))
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m λ\u001B[m=> "))

(defproject hexagram30/graphdb "0.1.0-SNAPSHOT"
  :description "A graph database for use by hexagram30 projects"
  :url "https://github.com/hexagram30/graphdb"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojusc/system-manager "0.3.0-SNAPSHOT"]
    [clojusc/twig "0.3.3"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [hexagram30/db-plugin "0.1.0-SNAPSHOT"]
    [org.clojure/clojure "1.9.0"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :dev {
      :exclusions [
        org.clojure/tools.namespace]
      :dependencies [
        [clojusc/trifl "0.3.0"]
        [org.clojure/tools.namespace "0.2.11"]]
      :plugins [
        [lein-shell "0.5.0"]
        [venantius/ultra "0.5.2"]]
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns hxgm30.graphdb.repl
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :lint {
      :exclusions [
        org.clojure/tools.namespace]
      :dependencies [
        [org.clojure/tools.namespace "0.2.11"]]
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.2.9"]
        [lein-ancient "0.6.15"]
        [lein-bikeshed "0.5.1"]
        [lein-kibit "0.1.6"]
        [venantius/yagni "0.1.4"]]}
    :test {
      :plugins [
        [lein-ltest "0.3.0"]]}
    :server {
      :jvm-opts ["-XX:MaxDirectMemorySize=512g"]
      :main hxgm30.graphdb.server}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;;;   Plugins   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :bitsy-plugin {
      :jvm-opts ["-Dgraphdb.backend=bitsy"]
      :dependencies [
        [hexagram30/bitsy-db-plugin "0.1.0-SNAPSHOT"]]}
    :janusgraph-plugin {
      :jvm-opts ["-Dgraphdb.backend=janusgraph"]
      :dependencies [
        [hexagram30/janusgraph-db-plugin "0.1.0-SNAPSHOT"]]}
    :orientdb-plugin {
      :jvm-opts ["-Dgraphdb.backend=orientdb"]
      :dependencies [
        [hexagram30/orient-db-plugin "0.1.0-SNAPSHOT"]]
      :aliases {
        "start-db" ["shell"
          "docker-compose"
            "-f" "plugins/orientdb/resources/docker/docker-compose-orientdb.yml"
            "up"]
        "stop-db" ["shell"
          "docker-compose"
            "-f" "plugins/orientdb/resources/docker/docker-compose-orientdb.yml"
            "down"]}}
    :redis-plugin {
      :jvm-opts [
        "-Dgraphdb.backend=redis"
        "-Dgraphdb.backend.subtype=graphdb"]
      :dependencies [
        [hexagram30/redis-db-plugin "0.1.0-SNAPSHOT"]]
      :aliases {
        ; "read-db-cfg" ["run" "-m" "hxgm30.db.plugin.docker" "read" "compose-redis-db.yml"]
        ; "start-db" ["run" "-m" "hxgm30.db.plugin.docker" "up" "compose-redis-db.yml"]
        ; "stop-db" ["run" "-m" "hxgm30.db.plugin.docker" "down" "compose-redis-db.yml"]
        "read-graphdb-cfg" ["run" "-m" "hxgm30.db.plugin.docker" "read" "compose-redis-graphdb.yml"]
        "start-graphdb" ["run" "-m" "hxgm30.db.plugin.docker" "up" "compose-redis-graphdb.yml"]
        "stop-graphdb" ["run" "-m" "hxgm30.db.plugin.docker" "down" "compose-redis-graphdb.yml"]}}
    :redisgraph-plugin {
      :jvm-opts ["-Dgraphdb.backend=redisgraph"]
      :dependencies [
        [hexagram30/redisgraph-db-plugin "0.1.0-SNAPSHOT"]]
      :aliases {
        "read-graphdb-cfg" ["run" "-m" "hxgm30.db.plugin.docker" "read" "compose-redisgraph-graphdb.yml"]
        "start-graphdb" ["run" "-m" "hxgm30.db.plugin.docker" "up" "compose-redisgraph-graphdb.yml"]
        "stop-graphdb" ["run" "-m" "hxgm30.db.plugin.docker" "down" "compose-redisgraph-graphdb.yml"]}}}
  :aliases {
    ;; Dev Aliases
    "repl" ["do"
      ["clean"]
      ["repl"]]
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
    "ltest-clean" ["do"
      ["clean"]
      ["ltest"]]
    "build" ["do"
      ["clean"]
      ["check-vers"]
      ["lint"]
      ["ltest" ":all"]
      ["uberjar"]]})
