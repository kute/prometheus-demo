package com.kute.demo.endpoint;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * created by bailong001 on 2019/08/12 16:47
 */
@WebEndpoint(id = "apple")
public class AppleScrapeEndPoint {

    private final CollectorRegistry collectorRegistry;

    public AppleScrapeEndPoint(CollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    @ReadOperation(produces = TextFormat.CONTENT_TYPE_004)
    public String scrape() {
        try {
            Writer writer = new StringWriter();
            TextFormat.write004(writer, this.collectorRegistry.metricFamilySamples());
            return writer.toString();
        } catch (IOException ex) {
            // This actually never happens since StringWriter::write() doesn't throw any
            // IOException
            throw new RuntimeException("Writing metrics failed", ex);
        }
    }
}
