# Walkthrough

## To Organize

The zone id gets pulled out of the system environment.

## Setup

### For local development

Base your `.lein-env` on `.lein-env-example`.

Start a local DynamoDB. See instructions in the `Dynamo.md` file.

Start a REPL with `lein repl`. Then use `(go)`.

This will initialize your DynamoDB database and start the Fondo server. You can
check it in a Web browser at [localhost:3000](http://localhost:3000).

### Using Cloud DynamoDB

Base your `.lein-env` on `.lein-env-example` and add your credentials.

Start a REPL with `lein repl`. Then use `(go)`.

This will initialize your DynamoDB database and start the Fondo server. You can
check it in a Web browser at [localhost:3000](http://localhost:3000).

## Storing a value

A value in Fondo must have at least the following metadata:

1. `name`: A string naming the value
2. `uri`: A valid URI where the data can be retrieved

Example:

```clj
{:name "Weather Data for Washington, DC, June 2014"
 :uri "http://s3.amazonaws.com/weather/dc/jun2014.csv"
 :description "Temperature and wind data are available."}
```

Use the convenience function in `dev/user.clj`:

```clj
(def weather-id
 (put-value
  {:name "Weather Data for Washington, DC, June 2014"
   :uri "https://s3.amazonaws.com/joshuamiller-fondo/Harrisburg+Crime+2014"
   :description "Temperature and wind data are available."}))
```

This returns the id as a string:

```clj
"6a87a0013f6d6ec60e70c39a8eeed4cb692a45144e61f3774ebba1640e0cf104a198a4c471835d5c12c7dba04fe42fe3"
```

## Getting a Value

```clj
(def weather-data (get-value weather-id))
```

You'll get a map with these keys: `[:data :description :name :uri]`. The `:data`
key contains the downloaded file.

## Putting a value and uploading to S3

There is not a convenience function.

```clj
(def dog-val
 {:name "Dog Breeds in 2015"
  :description "Currently living dog breeds as of 2015."})
(def dog-data
 (clojure.java.io/file "~/tmp/dogs.csv"))
(def dog-id
 (client/put-value (:node system) dog-val (:s3 system) dog-data "dogs.csv"))
```
