package com.example.consumer.web.controller;

import com.publicInterface.dto.User;
import com.publicInterface.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import part1.Server.integration.References;

/**
 * @Author: wt
 * @Description: TODO
 * @DateTime: 2024/11/08 15:53
 **/
@RestController
@RequestMapping("/api/consumer/test")
@Slf4j
public class testController {

    @References(version = "1.2")
    private UserService userService2;

    @References(version = "1.1")
    private UserService userService1;

    @RequestMapping(value = "/test1", method = {RequestMethod.GET})
    public Object page1() {
        User user = userService1.getUserByUserId(1);
        log.info(user.toString());
        return user;
    }

    @RequestMapping(value = "/test2", method = {RequestMethod.GET})
    public Object page2() {
        User user = userService2.getUserByUserId(2);
        log.info(user.toString());
        return user;
    }
}
