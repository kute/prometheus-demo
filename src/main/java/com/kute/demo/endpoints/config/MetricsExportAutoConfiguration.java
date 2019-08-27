package com.kute.demo.endpoints.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.joor.Reflect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.PropertiesConfigAdapter;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Map;

/**
 * created by bailong001 on 2019/08/24 10:12
 */
@Slf4j
@Component
@AutoConfigureAfter(value = {PrometheusMetricsExportAutoConfiguration.class})
@ConditionalOnClass(value = {PrometheusMeterRegistry.class})
public class MetricsExportAutoConfiguration implements BeanDefinitionRegistryPostProcessor,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public class AutoPropertiesConfigAdapter extends PropertiesConfigAdapter<PrometheusProperties>
            implements io.micrometer.prometheus.PrometheusConfig {

        AutoPropertiesConfigAdapter(PrometheusProperties properties) {
            super(properties);
        }

        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public boolean descriptions() {
            return get(PrometheusProperties::isDescriptions, io.micrometer.prometheus.PrometheusConfig.super::descriptions);
        }

        @Override
        public Duration step() {
            return get(PrometheusProperties::getStep, PrometheusConfig.super::step);
        }

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {

        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(DatagridEndpoint.class);
        if (CollectionUtils.isEmpty(beansMap)) {
            return;
        }

        Clock clock = applicationContext.getBean(Clock.class);
        Preconditions.checkNotNull(clock);

        for (Map.Entry<String, Object> entry : beansMap.entrySet()) {
            Object bean = entry.getValue();
            WebEndpoint webEndpoint = bean.getClass().getAnnotation(WebEndpoint.class);
            if (null == webEndpoint) {
                continue;
            }
            String endPointName = webEndpoint.id();
            if (Strings.isNullOrEmpty(endPointName)) {
                continue;
            }
            // prometheus properties bean
            BeanDefinitionBuilder prometheusPropertiesBeanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(PrometheusProperties.class);
            BeanDefinition prometheusPropertiesBeanDefinition = prometheusPropertiesBeanDefinitionBuilder.getRawBeanDefinition();
            ((DefaultListableBeanFactory) factory).registerBeanDefinition(endPointName + "PrometheusProperties", prometheusPropertiesBeanDefinition);

            PrometheusProperties prometheusProperties = applicationContext.getBean(endPointName + "PrometheusProperties", PrometheusProperties.class);

            // prometheus config bean
            BeanDefinitionBuilder prometheusConfigBeanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(AutoPropertiesConfigAdapter.class, () -> new AutoPropertiesConfigAdapter(prometheusProperties));
            BeanDefinition prometheusConfigBeanDefinition = prometheusConfigBeanDefinitionBuilder.getRawBeanDefinition();
            ((DefaultListableBeanFactory) factory).registerBeanDefinition(endPointName + "PrometheusConfig", prometheusConfigBeanDefinition);

            // collector registry bean
            BeanDefinitionBuilder collectorRegistryBeanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(CollectorRegistry.class, () -> new CollectorRegistry(true));
            BeanDefinition collectorRegistryBeanDefinition = collectorRegistryBeanDefinitionBuilder.getRawBeanDefinition();
            ((DefaultListableBeanFactory) factory).registerBeanDefinition(endPointName + "CollectorRegistry", collectorRegistryBeanDefinition);

            PrometheusConfig prometheusConfig = applicationContext.getBean(endPointName + "PrometheusConfig", AutoPropertiesConfigAdapter.class);
            CollectorRegistry collectorRegistry = applicationContext.getBean(endPointName + "CollectorRegistry", CollectorRegistry.class);

            // prometheus meter registry bean
            BeanDefinitionBuilder meterRegistryBeanDefinitionBuilder = BeanDefinitionBuilder
                    .genericBeanDefinition(PrometheusMeterRegistry.class, () -> new PrometheusMeterRegistry(prometheusConfig, collectorRegistry, clock));
            BeanDefinition meterRegistryBeanDefinition = meterRegistryBeanDefinitionBuilder.getRawBeanDefinition();
            ((DefaultListableBeanFactory) factory).registerBeanDefinition(endPointName + "MeterRegistry", meterRegistryBeanDefinition);

            Reflect.on(bean).set("collectorRegistry", collectorRegistry);

        }
    }
}
