(ns fondo.node
  (:require
   [cheshire.core :as json]
   [clojure.walk :refer [keywordize-keys]]
   [compojure.core :refer [routes GET PUT POST]]
   [compojure.route :as route]
   [environ.core :refer [env]]
   [fondo.db :as db]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [response]]))

(defn get-root
  [request]
  {:status 200
   :headers {}
   :body {:API ["GET /info"
                "GET /value/:id"
                "PUT /value/:id"]}})

(defn get-info
  [zone-id]
  {:status 200
   :headers {}
   :body {:fondo-version "0.1.0"
          :server ["fondo-clj" "0.1.0"]
          :zone-id zone-id}})

(defn get-value
  [db table-name id]
  (let [result (db/get-value db table-name id)]
    (if-let [val (:value result)]
      {:status 200
       :headers {}
       :body val}
      {:status 404
       :body result})))

(defn put-value
  [db table-name id request]
  (let [id (get-in request [:params :id])
        value (keywordize-keys (:body request))
        result (db/put-value db table-name id value)]
    (if-let [errors (:errors result)]
      {:status 422
       :body {:errors errors}}
      {:status 200
       :body result})))

(defn node-routes
  [db table-name zone-id]
  (routes
   (GET "/" [] get-root)
   (GET "/info" [] (get-info zone-id))
   (GET "/value/:id" [id] (get-value db table-name id))
   (PUT "/value/:id" [id :as request] (put-value db table-name id request))))

(defn node-app
  [db table-name zone-id]
  (-> (node-routes db table-name zone-id)
      wrap-json-response
      wrap-json-body))

(defn -main
  "Starts a Fondo node on port, using environ for
   AWS settings."
  [port]
  (let [db         {:access-key (env :dynamo-access-key)
                    :secret-key (env :dynamo-secret-key)
                    :endpoint   (env :dynamo-endpoint)}
        table-name (env :dynamo-table-name)
        zone-id    (env :zone-id)]
    (jetty/run-jetty (node-app db table-name zone-id) {:port (Integer. port)})))
