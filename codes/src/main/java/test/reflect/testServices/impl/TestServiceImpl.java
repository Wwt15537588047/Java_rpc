package test.reflect.testServices.impl;

import main.version1.v1.common.pojo.User;
import test.reflect.testServices.TestService;
import test.reflect.testServices.TestService1;

public class TestServiceImpl implements TestService, TestService1 {
    @Override
    public User getUserById(Integer id) {
        return null;
    }

    @Override
    public Integer insertUserById(User user) {
        return null;
    }

    @Override
    public User getUserByName(String name) {
        return null;
    }
}