(ns fondo.node
  (:require
   [cheshire.core :as json]
   [compojure.core :refer [routes GET]]
   [compojure.route :as route]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [response]]))

(def zone-id "12345")

(defn get-root
  [request]
  {:status 200
   :headers {}
   :body {:API ["GET /info"]}})

(defn get-info
  [request]
  {:status 200
   :headers {}
   :body {:fondo-version "0.1.0"
          :server ["fondo-clj" "0.1.0"]
          :zone-id zone-id}})

(defn get-value
  [request]
  nil)

(defn put-value
  [request]
  nil)

(def node-routes
  (routes
   (GET "/" [] get-root)
   (GET "/info" [] get-info)
   (GET "/value/" [] get-value)
   (PUT "/value" [] put-value)))

(def node-app
  (-> node-routes
      wrap-json-response))
