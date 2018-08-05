;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Supplemental, Backend-specific API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn-db find-keys pattern)
(defn-db latency-doctor)
(defn-db latency-latest)

(defn latency-setup
  ([]
    (backend/db-call (system) 'latency-setup))
  ([milliseconds]
    (backend/db-call (system) 'latency-setup milliseconds)))

(defn slowlog
  ([]
    (backend/db-call (system) 'slowlog))
  ([count]
    (backend/db-call (system) 'slowlog count)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Third-party Tools   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require
  '[loom.alg :as alg]
  '[loom.alg-generic :as alg-generic]
  '[loom.attr :as attr]
  '[loom.flow :as flow]
  '[loom.graph :as graph]
  '[loom.io :as loom-io])

(require
  '[hxgm30.graphdb.plugin.redis.api.queries :as queries])
