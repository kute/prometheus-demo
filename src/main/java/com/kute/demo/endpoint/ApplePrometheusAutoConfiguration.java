package com.kute.demo.endpoint;

import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * created by bailong001 on 2019/08/12 16:30
 */
@Configuration
@AutoConfigureAfter(value = {PrometheusMetricsExportAutoConfiguration.class})
@ConditionalOnClass(value = {PrometheusMeterRegistry.class})
@ConditionalOnProperty(prefix = "management.metrics.export.apple", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class ApplePrometheusAutoConfiguration {

    @Bean(name = "applePrometheusProperties")
    @ConfigurationProperties(prefix = "management.metrics.export.apple")
    public PrometheusProperties applePrometheusProperties() {
        return new PrometheusProperties();
    }

    @Bean(name = "applePrometheusConfig")
    public PrometheusConfig applePrometheusConfig() {
        return new ApplePropertiesConfigAdapter(applePrometheusProperties());
    }

    @Bean(name = "appleMeterRegistry")
    public PrometheusMeterRegistry appleMeterRegistry(Clock clock) {
        return new PrometheusMeterRegistry(applePrometheusConfig(), appleCollectorRegistry(), clock);
    }

    @Bean(name = "appleCollectorRegistry")
    public CollectorRegistry appleCollectorRegistry() {
        System.out.println("=======appleCollectorRegistry");
        return new CollectorRegistry(true);
    }

    @Configuration
    @ConditionalOnEnabledEndpoint(endpoint = AppleScrapeEndPoint.class)
    public static class TicketScrapeEndpointConfiguration {

        @Resource
        private CollectorRegistry appleCollectorRegistry;

        @Bean(name = "appleEndpoint")
        @ConditionalOnMissingBean
        public AppleScrapeEndPoint appleEndpoint() {
            return new AppleScrapeEndPoint(appleCollectorRegistry);
        }

    }

}
