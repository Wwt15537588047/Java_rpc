package main.version1.v3.common.service;

import main.version1.v3.common.pojo.User;


public interface UserService {
    User getUserById(Integer id);
    Integer insertUserById(User user);
}
