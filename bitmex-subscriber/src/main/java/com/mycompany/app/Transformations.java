package com.mycompany.app;

import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class Transformations {
    public static final Logger LOG = LoggerFactory.getLogger(Transformations.class);

    public static class JsonToTableRow extends DoFn<String, TableRow> {
        /** Converts UTF8 encoded Json records to TableRow records. */
        @ProcessElement
        public void processElement(ProcessContext c) {
            TableRow row;
            String json = c.element();

            // Parse the JSON into a {@link TableRow} object.
            try (InputStream inputStream =
                         new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                row = TableRowJsonCoder.of().decode(inputStream, Coder.Context.OUTER);
                String action = (String) row.get("action");
                String table = (String) row.get("table");
                if (Objects.equals(action, "partial") || action == null) {
                    LOG.warn("row omitted: {}", row);

                }
                else if (Objects.equals(table, "orderBookL2_25")) {
                    LOG.info("row: {}", row);
                    c.output(row);
                }
            } catch (IOException e) {
                LOG.error("Failed to serialize json to table row: " + json, e);
            }
        }
    }

    public static class logElement extends DoFn<String, Void> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            LOG.info("Received at {} : {}", Instant.now(), c.element());
        }
    }
}
