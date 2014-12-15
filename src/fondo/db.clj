(ns fondo.db)

(def values (atom {}))

(defn put-value
  [id val]
  (swap! values assoc id val))

(defn get-value
  [id]
  (@values id))
