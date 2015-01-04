(ns fondo.node
  (:require
   [cheshire.core :as json]
   [clojure.walk :refer [keywordize-keys]]
   [compojure.core :refer [routes GET PUT POST]]
   [compojure.route :as route]
   [fondo.db :as db]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [response]]))

(def zone-id "12345")

(defn get-root
  [request]
  {:status 200
   :headers {}
   :body {:API ["GET /info"
                "GET /value/:id"
                "PUT /value/:id"]}})

(defn get-info
  [request]
  {:status 200
   :headers {}
   :body {:fondo-version "0.1.0"
          :server ["fondo-clj" "0.1.0"]
          :zone-id zone-id}})

(defn get-value
  [request]
  (let [id (get-in request [:params :id])
        result (db/get-value id)]
    (if-let [val (:value result)]
      {:status 200
       :headers {}
       :body val}
      {:status 404
       :body result})))

(defn put-value
  [request]
  (let [id (get-in request [:params :id])
        value (keywordize-keys (:body request))
        result (db/put-value id value)]
    (if-let [errors (:errors result)]
      {:status 422
       :body errors}
      {:status 200
       :body result})))

(def node-routes
  (routes
   (GET "/" [] get-root)
   (GET "/info" [] get-info)
   (GET "/value/:id" [] get-value)
   (PUT "/value/:id" [] put-value)))

(def node-app
  (-> node-routes
      wrap-json-response
      wrap-json-body))
