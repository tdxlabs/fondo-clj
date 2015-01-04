(ns fondo.client
  (:require
   [cheshire.core :as json]
   [clj-http.client :as client]
   [clojurewerkz.urly.core :as url]
   [fondo.encode :refer [encode-and-hash]]))

(defn get-value
  "Retrieve a value from a Fondo node. `node` should be a map
   with a key `:url` denoting its location."
  [node id]
  (let [url  (url/url-like (:url node))
        path (.mutatePath url (str "value/" id))
        resp (:body (client/get (str path) {:as :json
                                            :coerce :always
                                            :throw-exceptions false}))]
    resp))

(defn put-value
  "Put a value into node; hashes val to return the ID that will be used.
   If unsuccessful, errors are returned in a map."
  [node val]
  (let [id   (encode-and-hash val)
        url  (url/url-like (:url node))
        path (.mutatePath url (str "value/" id))
        resp (:body (client/put (str path) {:as :json
                                            :coerce :always
                                            :content-type :json
                                            :throw-exceptions false
                                            :body (json/encode val)}))]
    (if-let [id (:vid resp)]
      id
      resp)))
