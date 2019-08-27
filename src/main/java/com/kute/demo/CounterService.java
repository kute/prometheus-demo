package com.kute.demo;

import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by bailong001 on 2019/07/16 14:49
 */
@Service
public class CounterService {

    @Resource
    private PrometheusMeterRegistry fruitMeterRegistry;
    @Resource
    private PrometheusMeterRegistry appleMeterRegistry;
    @Resource
    private PrometheusMeterRegistry aMeterRegistry;
    @Resource
    private PrometheusMeterRegistry bMeterRegistry;

    public Object incrCount(String type, String value) {
        String name = "user.counter." + type;
        if ("apple".equals(type)) {
            return appleMeterRegistry.gauge(name, new AtomicDouble(Double.parseDouble(value))).get();
        } else if ("fruit".equals(type)) {
            return fruitMeterRegistry.gauge(name, new AtomicDouble(Double.parseDouble(value))).get();
        } else if("a".equals(type)) {
            return aMeterRegistry.gauge(name, new AtomicDouble(Double.parseDouble(value))).get();
        } else if("b".equals(type)) {
            return bMeterRegistry.gauge(name, new AtomicDouble(Double.parseDouble(value))).get();
        }
        return "null";
    }
}
