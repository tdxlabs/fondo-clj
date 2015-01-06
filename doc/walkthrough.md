# Walkthrough

## Setup

### For local development

Note that `zone-id` is hard-coded in `fondo.node`.

Start a local DynamoDB.

`lein init-db`

### Initializing a zone

Currently, `zone-id` is hard-coded.

Start multiple nodes like this:
* `lein ring server 3000`
* `lein ring server 3001`

### Using Cloud DynamoDB

TODO

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

TODO: Allow data to be provided and push through to URI
