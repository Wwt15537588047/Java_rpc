package client;
import client.proxy.ClientProxy;
import common.pojo.User;
import common.service.UserService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy=new ClientProxy();
        UserService proxy=clientProxy.getProxy(UserService.class);

        User user = proxy.getUserById(1);
        System.out.println("从服务端得到的user="+user.toString());

        User u= User.builder().id(100).userName("wxx").sex(true).build();
        Integer id = proxy.insertUserById(u);
        System.out.println("向服务端插入user的id"+id);
    }
}
