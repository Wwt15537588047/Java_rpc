package proxy.jdk.imp;

import proxy.jdk.GetUserNameById;

import java.util.UUID;

/**
 * @Author wt
 * @Description JDK动态代理接口实现类
 * @Data 2024/12/3 下午2:38
 */
public class GetUserNameByIdImp implements GetUserNameById {
    @Override
    public String getUserName(Integer id) {
        String namePrev = UUID.randomUUID().toString().substring(0, 10);
        return namePrev + "." + id;
    }
}
