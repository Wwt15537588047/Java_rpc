package main.version1.v1.client;
import lombok.extern.slf4j.Slf4j;
import main.version1.v1.common.pojo.User;
import main.version1.v1.client.proxy.ClientProxy;
import main.version1.v1.common.service.UserService;


@Slf4j
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        UserService proxy = clientProxy.getProxy(UserService.class);
        User user = proxy.getUserById(12);
        log.info("从客户端接收到的User : {}", user);
        User u=User.builder().id(100).userName("wxx").sex(true).build();
        Integer id = proxy.insertUserById(u);
        log.info("向服务端插入的用户Id:{}",id);
    }
}
