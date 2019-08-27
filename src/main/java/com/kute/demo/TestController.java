package com.kute.demo;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * created by bailong001 on 2019/07/16 16:17
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private CounterService counterService;

    @GetMapping("/call/{type}")
    public Object callIn(@PathVariable String type, @RequestParam String value) {
        return counterService.incrCount(type, value);
    }

}
