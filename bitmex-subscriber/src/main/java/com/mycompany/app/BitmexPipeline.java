package com.mycompany.app;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED;
import static org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition.WRITE_APPEND;

public class BitmexPipeline {

    public static final Logger LOG = LoggerFactory.getLogger(BitmexPipeline.class);

    public static void main(String[] args) {

        new ArrayList<TableFieldSchema>() {
            {
                add(new TableFieldSchema().setName("symbol").setType("STRING"));
                add(new TableFieldSchema().setName("id").setType("STRING"));
                add(new TableFieldSchema().setName("side").setType("INT64"));
                add(new TableFieldSchema().setName("size").setType("INT64"));
                add(new TableFieldSchema().setName("price").setType("FLOAT64"));
                add(new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"));
            }
        };

        TableSchema orderBookTableSchema = new TableSchema()
                .setFields(Arrays.asList(
                        new TableFieldSchema()
                                .setName("table")
                                .setType("STRING")
                                .setMode("NULLABLE"),
                        new TableFieldSchema()
                                .setName("action")
                                .setType("STRING")
                                .setMode("NULLABLE"),
                        new TableFieldSchema()
                                .setName("data")
                                .setType("RECORD")
                                .setMode("REPEATED")
                                .setFields(new ArrayList<TableFieldSchema>() {
                                    {
                                        add(new TableFieldSchema().setName("symbol").setType("STRING"));
                                        add(new TableFieldSchema().setName("id").setType("STRING"));
                                        add(new TableFieldSchema().setName("side").setType("STRING"));
                                        add(new TableFieldSchema().setName("size").setType("INT64"));
                                        add(new TableFieldSchema().setName("price").setType("FLOAT64"));
                                        add(new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"));
                                    }
                                })));

        PipelineOptionsFactory.register(BitmexPipelineOptions.class);
        BitmexPipelineOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(BitmexPipelineOptions.class);

        Pipeline pipeline = Pipeline.create(options);

        System.out.println("Options: " + options);

        LOG.info("Starting stream processing");
        pipeline
                .apply("ReadStringFromPubsub",
                        PubsubIO.readStrings().fromTopic(options.getPubsubTopic()))
                .apply("ToTableRow", ParDo.of(new Transformations.JsonToTableRow()))
                .apply("WriteToBigQuery", BigQueryIO.writeTableRows()
                        .to(String.format("%s.%s.%s", "bitmex-stream", "bitmex_dataset", "orderBookL2_25"))
                        .withCreateDisposition(CREATE_IF_NEEDED)
                        .withWriteDisposition(WRITE_APPEND)
                        .withMethod(BigQueryIO.Write.Method.STREAMING_INSERTS)
                        .withSchema(orderBookTableSchema)
                );
        pipeline.run();
        LOG.info("End of streaming job");
    }
}