(ns fondo.client
  (:require
   [aws.sdk.s3 :as s3]
   [cheshire.core :as json]
   [clj-http.client :as client]
   [clojurewerkz.urly.core :as url]
   [fondo.encode :refer [encode-and-hash val-with-data]]))

(defn get-value
  "Retrieve a value from a Fondo node. `node` should be a map
   with a key `:url` denoting its location. Ensures result matches
   hashed ID."
  [node id]
  (let [url  (url/url-like (:url node))
        path (.mutatePath url (str "value/" id))
        resp (:body (client/get (str path) {:as :json
                                            :coerce :always
                                            :throw-exceptions false}))]
    (if (:error resp)
      resp
      (let [with-data (val-with-data resp)]
        (if (= id (encode-and-hash with-data))
          with-data
          {:error "hash-failure"})))))

(defn ^:internal ensure-bucket-exists
  "Make sure the S3 bucket with bucket-name exists, create it if it
  does not"
  [s3]
  (if-not (s3/bucket-exists? (:cred s3) (:bucket-name s3))
    (s3/create-bucket (:cred s3) (:bucket-name s3))))

(defn ^:internal put-to-s3
  "Post a String, InputStream, or File to S3 as specified by
  object-name. File will be made public."
  [s3 object-name data]
  (s3/put-object (:cred s3) (:bucket-name s3) object-name data)
  (s3/update-object-acl (:cred s3)
                        (:bucket-name s3)
                        object-name
                        (s3/grant :all-users :read))
  (str "https://s3.amazonaws.com/" (:bucket-name s3) "/" object-name))

(defn put-value
  "Put a value into node; hashes val to return the ID that will be used.
   If unsuccessful, errors are returned in a map. Optionally include
   S3 credentials and a String, InputStream, or File to have the data
   posted to S3 first."
  ([node val]
   (let [id   (encode-and-hash (val-with-data val))
         url  (url/url-like (:url node))
         path (.mutatePath url (str "value/" id))
         resp (:body (client/put (str (url/url-like path))
                                 {:as :json
;                                  :coerce :always
                                  :content-type :json
                                  :throw-exceptions false
                                  :body (json/encode val)}))]
     (if-let [id (:vid resp)]
       id
       resp)))
  ([node val s3 data s3-object-name]
   (ensure-bucket-exists s3)
   (let [uri (put-to-s3 s3 s3-object-name data)]
     (put-value node (assoc val :uri uri)))))
