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
  :description "A graph database, built on OrientDB, for use by hexagram30 projects"
  :url "https://github.com/hexagram30/graphdb"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojusc/system-manager "0.3.0-SNAPSHOT"]
    [clojusc/twig "0.3.2"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [org.clojure/clojure "1.8.0"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :dev {
      :exclusions [
        org.clojure/tools.namespace]
      :dependencies [
        [clojusc/trifl "0.2.0"]
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
        [jonase/eastwood "0.2.5"]
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
    :bitsy-plugin {
      :source-paths ["plugins/bitsy"]
      :dependencies [
        [com.lambdazen.bitsy/bitsy "3.0.2"]]}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;;;   Plugins   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :janusgraph-plugin {
      :jvm-opts ["-Dgraph.backend=janusgraph"]
      :source-paths ["plugins/janusgraph"]
      :dependencies [
        [org.apache.tinkerpop/gremlin-server "3.2.7"]
        [org.janusgraph/janusgraph-berkeleyje "0.2.0"]]}
    :orientdb-plugin {
      :jvm-opts ["-Dgraph.backend=orientdb"]
      :source-paths ["plugins/orientdb"]
      :resource-paths ["plugins/orientdb/resources"]
      :dependencies [
        [clojurewerkz/ogre "3.3.1.0"]
        [com.orientechnologies/orientdb-client "2.2.33"]
        [com.orientechnologies/orientdb-core "2.2.33"]
        [com.orientechnologies/orientdb-graphdb "2.2.33"]
        [com.tinkerpop.blueprints/blueprints-core "2.6.0"]]
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
      :jvm-opts ["-Dgraph.backend=redis"]
      :source-paths [
        "plugins/redis/src"
        "plugins/redis/dev"]
      :resource-paths ["plugins/redis/resources"]
      :dependencies [
        [aysylu/loom "1.0.1"]
        [clojusc/trifl "0.2.0"]
        [com.taoensso/carmine "2.18.0"]]
      :aliases {
        "start-db" ["shell"
          "docker-compose"
            "-f" "plugins/redis/resources/docker/docker-compose-redis.yml"
            "up"]
        "stop-db" ["shell"
          "docker-compose"
            "-f" "plugins/redis/resources/docker/docker-compose-redis.yml"
            "down"]}}
    :redisgraph-plugin {
      :jvm-opts ["-Dgraph.backend=redisgraph"]
      :source-paths ["plugins/redisgraph"]
      :resource-paths ["plugins/redisgraph/resources"]
      :dependencies [
        [com.taoensso/carmine "2.18.0"]]
      :aliases {
        "start-db" ["shell"
          "docker-compose"
            "-f" "plugins/redisgraph/resources/docker/docker-compose-redis-graph.yml"
            "up"]
        "stop-db" ["shell"
          "docker-compose"
            "-f" "plugins/redisgraph/resources/docker/docker-compose-redis-graph.yml"
            "down"]}}}
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
    "ltest" ["with-profile" "+test" "ltest"]})
