package com.kute.demo;

import com.google.common.collect.Lists;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.cumulative.CumulativeFunctionCounter;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.vavr.control.Try;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import static java.util.function.Predicate.isEqual;


/**
 * created by bailong001 on 2019/08/11 10:50
 */
public class Test {

    public static void main(String[] args) {

//        SimpleMeterRegistry registry = new SimpleMeterRegistry();
//
//        AtomicInteger integer = new AtomicInteger(0);
//
//        FunctionCounter counter = registry.more().counter("a", null, integer, AtomicInteger::get);
//
//        System.out.println(counter.count());
//        System.out.println(counter.getId());
//
//        integer.addAndGet(-4);
//        System.out.println(counter.count());
//        System.out.println(registry.more().counter("a", null, integer, AtomicInteger::get).count());
//        integer.set(0);
//        System.out.println(registry.more().counter("a", null, 0).count());
//        System.out.println(registry.get("a").meter().getId());
//        System.out.println(registry.find("a").meter().getId());
//        System.out.println(Search.in(registry).name("a").counter());
//        System.out.println(Search.in(registry).name("a").functionCounters());
//
//        System.out.println(counter.hashCode());
//        System.out.println(registry.getMeters().get(0).getId());
//        System.out.println(registry);
//
//        List<FunctionCounter> list = getBySearch("a", null, FunctionCounter.class, registry);

//        List<Tag> tagList = Lists.newArrayList();
////        tagList.add(Tag.of("system", "kute"));
////        tagList.add(Tag.of("dimension", "agent"));
////
////        Timer timer = registry.timer("timer", tagList);
////        timer.record(new Thread(() -> {
////            try {
////                Thread.sleep(3000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }));
////
////        Timer.Sample sample = Timer.start(registry);
////        new Thread(() -> {
////            try {
////                Thread.sleep(3000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////            sample.stop(registry.timer("timer", tagList));
////        }).start();
////
////        System.out.println(getBySearch("timer", tagList, Timer.class, registry));


        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 1);
        map.forEach((key, value) -> {
            System.out.println(key);
            if ("a".equals(key)) {
                return;
            }
            System.out.println(key + value);
        });

    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getBySearch(String name, List<Tag> tagList, Class<T> tClass, SimpleMeterRegistry registry) {
        return (List<T>) Try.of(() -> {
            Search search = Search.in(registry)
                    .name(name);
            if (!CollectionUtils.isEmpty(tagList)) {
                search.tags(tagList);
            }
            return Lists.newArrayList(Match(tClass).of(
                    Case($(isEqual(Counter.class)), search::counters),
                    Case($(isEqual(Timer.class)), search::timers),
                    Case($(isEqual(FunctionCounter.class)), () -> {
                        System.out.println("done");
                        return search.functionCounters();
                    }),
                    Case($(), Collections::emptyList)
            ));
        })
                .onFailure(Throwable::printStackTrace)
                .getOrNull();
    }
}
