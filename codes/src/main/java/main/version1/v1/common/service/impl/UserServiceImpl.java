package main.version1.v1.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import main.version1.v1.common.pojo.User;
import main.version1.v1.common.service.UserService;

import java.util.Random;
import java.util.UUID;

@Slf4j
public class UserServiceImpl implements UserService {

    public User getUserById(Integer id) {
        log.info("[GetUserById] server find user success, user id is :{}",id);
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean()).build();
        return user;
    }

    public Integer insertUserById(User user) {
        log.info("[insertUserById]insert user success, user name : {} ",user.getUserName());
        return user.getId();
    }
}
