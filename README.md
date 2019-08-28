# kafka-connect-log-analytics

## Log analytics Sink Connector for Kafka-connect
* this connector pull messages from Kafka topic and send to log analytics custom logs
* Only supported message format is JSON with schema, make sure valid json is present in Kafka topic
* Batch 100 records at a time for a given poll cycle

## Connector Definition

 {	<br />
	“name”:”sink-connector-1”<br />
	"connector.class": "io.kafka.connect.log.anlaytics.sink.LogAnlayticsSinkTask",<br />
	"tasks.max": "1",<br />
	"workspaceId":"xxxxx-xxxx-xxxx-xxxx-xxxxxxxx",<br />
	"workspacekey":"xxxxxxxxxx==",<br />
	"topics": "DATASINKLOGS"<br />
}
 