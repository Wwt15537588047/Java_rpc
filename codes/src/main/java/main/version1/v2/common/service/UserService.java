package main.version1.v2.common.service;

import main.version1.v2.common.pojo.User;


public interface UserService {
    User getUserById(Integer id);
    Integer insertUserById(User user);
}
