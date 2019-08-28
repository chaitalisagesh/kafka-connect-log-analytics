# kafka-connect-log-analytics

## Log analytics Sink Connector for Kafka-connect
* this connector pull messages from Kafka topic and send to log analytics custom logs
* Only supported message format is JSON with schema, make sure valid json is present in Kafka topic
* Batch 100 records at a time for a given poll cycle

## Connector Definition

 {	
	“name”:”sink-connector-1”
	"connector.class": "io.kafka.connect.log.anlaytics.sink.LogAnlayticsSinkTask",
	"tasks.max": "1",
	"workspaceId":"xxxxx-xxxx-xxxx-xxxx-xxxxxxxx",
	"workspacekey":"xxxxxxxxxx==",
	"topics": "DATASINKLOGS"
}
 