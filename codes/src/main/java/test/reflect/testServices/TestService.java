package test.reflect.testServices;

import main.version1.v1.common.pojo.User;

public interface TestService {
    User getUserById(Integer id);
    Integer insertUserById(User user);
}
