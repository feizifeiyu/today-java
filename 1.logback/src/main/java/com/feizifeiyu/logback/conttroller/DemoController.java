package com.feizifeiyu.logback.conttroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 非子非鱼
 * @date: 2019/7/19 下午3:10
 */
@RestController
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("hello")
    public Object hello(){

        LOGGER.info("INFO级别日志");
        LOGGER.warn("WARN级别日志");
        LOGGER.error("错误级别日志");

        return "hello logback";
    }

}
