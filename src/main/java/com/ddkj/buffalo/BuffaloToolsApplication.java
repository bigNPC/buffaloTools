package com.ddkj.buffalo;

import com.ddkj.buffalo.controller.StartController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.annotation.Resource;

/**
 * create by xzb 2021.1.12
 */
@SpringBootApplication
public class BuffaloToolsApplication implements CommandLineRunner{
    @Resource
    StartController startController;

    public static void main(String[] args) {
        //awt null 处理
        SpringApplicationBuilder builder = new SpringApplicationBuilder(BuffaloToolsApplication.class);
        builder.headless(false).run(args);
    }

    @Override
    public void run(String... args) {
        startController.initFrame();
    }
}
