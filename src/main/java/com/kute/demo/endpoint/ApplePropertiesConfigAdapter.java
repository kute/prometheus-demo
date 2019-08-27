package com.kute.demo.endpoint;

import io.micrometer.prometheus.PrometheusConfig;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.PropertiesConfigAdapter;

import java.time.Duration;

/**
 * created by bailong001 on 2019/08/12 17:56
 */
public class ApplePropertiesConfigAdapter extends PropertiesConfigAdapter<PrometheusProperties>
        implements PrometheusConfig {

    ApplePropertiesConfigAdapter(PrometheusProperties properties) {
        super(properties);
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public boolean descriptions() {
        return get(PrometheusProperties::isDescriptions, PrometheusConfig.super::descriptions);
    }

    @Override
    public Duration step() {
        return get(PrometheusProperties::getStep, PrometheusConfig.super::step);
    }

}
