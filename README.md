# kafka-connect-log-analytics

## Log analytics Sink Connector for Kafka-connect
* this connector pull messages from Kafka topic and send to log analytics custom logs
* Only supported message format is JSON with schema, make sure valid json is present in Kafka topic
* Batch 100 records at a time for a given poll cycle
* This derives log analytics columns based on the schema of the message received
* Messages appear in log analytics under "Custom Logs" with "_CL" as post fix to given topic i.e. if "DATASINKLOGS" is source topic then messages will be displayed in log analytics under custom logs with name "DATASINKLOGS_CL"

## Connector Definition

 {	<br />
	“name”:”sink-connector-1”<br />
	"connector.class": "io.kafka.connect.log.anlaytics.sink.LogAnlayticsSinkTask",<br />
	"tasks.max": "1",<br />
	"workspace.id":"xxxxx-xxxx-xxxx-xxxx-xxxxxxxx",<br />
	"workspace.key":"xxxxxxxxxx==",<br />
	"topics": "DATASINKLOGS"<br />
}
 