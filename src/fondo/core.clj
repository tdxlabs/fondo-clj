(ns fondo.core
  (:require
   [cheshire.core :as json]
   [compojure.core :refer [routes GET]]
   [compojure.route :as route]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [response]]
   ))

(comment (clojure.tools.reader.edn/read))

(def zone-id "12345")

(defn node-root
  [request]
  {:status 200
   :headers {}
   :body {:API ["GET /info"]}})

(defn node-info
  [request]
  {:status 200
   :headers {}
   :body {:fondo-version "0.1.0"
          :server ["fondo-clj" "0.1.0"]
          :zone-id zone-id}})

(def node-routes
  (routes
   (GET "/" [] node-root)
   (GET "/info" [] node-info)))

(def node-app
  (-> node-routes
      wrap-json-response))

;; FUTURE:
;; * use DynamoDB
;; * add authentication

