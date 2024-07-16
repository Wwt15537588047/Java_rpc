package main.version1.v1.common.service;

import main.version1.v1.common.pojo.User;


public interface UserService {
    User getUserById(Integer id);
    Integer insertUserById(User user);
}
