package com.kute.demo.endpoint.fruit;

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
@ConditionalOnProperty(prefix = "management.metrics.export.fruit", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class FruitPrometheusAutoConfiguration {

    @Bean(name = "fruitPrometheusProperties")
    @ConfigurationProperties(prefix = "management.metrics.export.fruit")
    public PrometheusProperties fruitPrometheusProperties() {
        return new PrometheusProperties();
    }

    @Bean(name = "fruitPrometheusConfig")
    public PrometheusConfig fruitPrometheusConfig() {
        return new FruitPropertiesConfigAdapter(fruitPrometheusProperties());
    }

    @Bean(name = "fruitMeterRegistry")
    public PrometheusMeterRegistry fruitMeterRegistry(Clock clock) {
        return new PrometheusMeterRegistry(fruitPrometheusConfig(), fruitCollectorRegistry(), clock);
    }

    @Bean(name = "fruitCollectorRegistry")
    public CollectorRegistry fruitCollectorRegistry() {
        System.out.println("=======fruitCollectorRegistry");
        return new CollectorRegistry(true);
    }

    @Configuration
    @ConditionalOnEnabledEndpoint(endpoint = FruitScrapeEndPoint.class)
    public static class fruitScrapeEndpointConfiguration {

        @Resource
        private CollectorRegistry fruitCollectorRegistry;

        @Bean(name = "fruitEndpoint")
        @ConditionalOnMissingBean
        public FruitScrapeEndPoint fruitEndpoint() {
            return new FruitScrapeEndPoint(fruitCollectorRegistry);
        }

    }

}
