package com.mycompany.app;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class BitmexPipeline {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitmexPipeline.class);

    public static void main(String[] args) {
        LOGGER.info("Starting stream processing");

        PipelineOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(BitmexPipelineOptions.class);

        Pipeline pipeline = Pipeline.create(options);

        pipeline
                .apply("ReadStrinsFromPubsub",
                        PubsubIO.readStrings().fromTopic("/topics/bitmex-stream/bitmex-topic"))
                .apply("PrintToStdout", ParDo.of(new DoFn<String, Void>() {
                    @DoFn.ProcessElement
                    public void processElement(ProcessContext c) {
                        LOGGER.info("Received at {} : {}", Instant.now(), c.element());
                    }
                }));

        pipeline.run();

        LOGGER.info("End of streaming job");
    }
}