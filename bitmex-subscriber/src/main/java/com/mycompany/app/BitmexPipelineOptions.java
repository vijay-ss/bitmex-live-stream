package com.mycompany.app;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface BitmexPipelineOptions extends PipelineOptions {
    @Description("Pubsub topic")
    String getPubsubTopic();
    void setPubsubTopic(String pubsubTopic);
}
