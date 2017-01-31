# Scholix Consumer API

## Setup

* Download Elasticsearch (5.1): https://www.elastic.co/downloads/elasticsearch
* Change owner of elasticsearch directory to user
* Install the JSON processor JQ (e.g. "apt-get install jq"): https://stedolan.github.io/jq/
* Clone this repository

## Compile

```
mvn package assembly:single
```

## Run

```
ES_JAVA_OPTS="-Xms512m -Xmx512m" bin/elasticsearch -d
cli/import data.json
java -jar target/scholix-consumer-api-0.0.1-SNAPSHOT-jar-with-dependencies.jar 2>&1 | tee /tmp/scholix-consumer-api.log &
```

## Examples

* /publications/dataset?identifier=10.1594/PANGAEA.186179
* /datasets/publication?identifier=10.1126/science.191.4232.1131
* /datasets/dataset?identifier=10.6085/aa/cblx00_xxxitbdxlsr02_20050511.50.2

## Notes

* The services runs at http://localhost:8282 (current default)
* The service assumes Elasticsearch runs at localhost:9300
* The first request after starting the service takes longer. This is due to creating the connection to Elasticsearch. May be fixed by incurring the start up cost at the time of starting the service, rather than at the time of answering the first request.