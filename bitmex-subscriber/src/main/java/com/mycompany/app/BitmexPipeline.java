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
    public static final Logger LOG = LoggerFactory.getLogger(BitmexPipeline.class);

    public static void main(String[] args) {
        LOG.info("Starting stream processing");

        PipelineOptionsFactory.register(BitmexPipelineOptions.class);
        PipelineOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(BitmexPipelineOptions.class);

        Pipeline pipeline = Pipeline.create(options);

        pipeline
                .apply("ReadStrinsFromPubsub",
                        PubsubIO.readStrings().fromTopic(((BitmexPipelineOptions) options).getPubsubTopic()))
                .apply("PrintToStdout", ParDo.of(new DoFn<String, Void>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        LOG.info("Received at {} : {}", Instant.now(), c.element());
                    }
                }));

        pipeline.run();

        LOG.info("End of streaming job");
    }
}