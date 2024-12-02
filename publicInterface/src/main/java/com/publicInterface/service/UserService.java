package com.publicInterface.service;

import com.publicInterface.dto.User;

/**
 * @Author wt
 * @Description 公共接口,供客户端和服务端共同使用
 * @Data 2024/12/2 下午8:26
 */

public interface UserService {
    // 客户端通过这个接口调用服务端的实现类
    User getUserByUserId(Integer id);
    //新增一个功能
    Integer insertUserId(User user);
}
