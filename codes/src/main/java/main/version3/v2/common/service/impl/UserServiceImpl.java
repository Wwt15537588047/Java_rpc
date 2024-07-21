package main.version3.v2.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import main.version3.v2.common.pojo.User;
import main.version3.v2.common.service.UserService;

import java.util.Random;
import java.util.UUID;

@Slf4j
public class UserServiceImpl implements UserService {

    public User getUserById(Integer id) {
        log.info("服务端查询了id :{}的用户",id);
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean()).build();
        return user;
    }

    public Integer insertUserById(User user) {
        System.out.println("插入数据成功"+user.getUserName());
        return user.getId();
    }
}
