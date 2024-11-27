package main.version1.v1.client;
import lombok.extern.slf4j.Slf4j;
import main.version1.v1.common.pojo.User;
import main.version1.v1.client.proxy.ClientProxy;
import main.version1.v1.common.service.UserService;


@Slf4j
public class TestClient {
    public static void main(String[] args) {
        // 创建ClientProxy对象
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        // 通过ClientProxy对象获取代理对象
        UserService proxy = clientProxy.getProxy(UserService.class);
        // 调用代理对象的方法
        User user = proxy.getUserById(12);
        log.info("[main] client receive User : {}", user);
        User u=User.builder().id(100).userName("wxx").sex(true).build();
        Integer id = proxy.insertUserById(u);
        log.info("[main] client insert user to server Id is :{}",id);
    }
}
