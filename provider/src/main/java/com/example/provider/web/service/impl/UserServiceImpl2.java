package com.example.provider.web.service.impl;

import com.publicInterface.dto.User;
import com.publicInterface.service.UserService;
import part1.Server.integration.RpcService;

import java.util.Random;
import java.util.UUID;

/**
 * @Author: yty
 * @Description: TODO
 * @DateTime: 2024/11/08 15:05
 **/
@RpcService(version = "1.2")
public class UserServiceImpl2 implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("实现1.2:客户端查询了"+id+"的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        return User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean()).build();
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("实现1.2:插入数据成功"+user.getUserName());
        return user.getId();
    }
}