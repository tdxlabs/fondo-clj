(ns fondo.util
  (:require
   [compojure.core :refer [routes GET]]
   [ring.adapter.jetty :as jetty]))

(def echo-app
  (routes
   (GET "/" [] "echo")))

(defn run-echo-app
  [port]
  (jetty/run-jetty echo-app {:port (Integer. port)}))
