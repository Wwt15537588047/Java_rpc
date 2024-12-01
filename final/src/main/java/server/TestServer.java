package server;


import common.service.UserService;
import common.service.impl.UserServiceImpl;
import server.provider.ServiceProvider;
import server.server.RPCServer;
import server.server.impl.NettyRPCServerImpl;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        /**
         * 当前服务包含三个提供者：ServiceProvider、RateLimitProvider、ZKServiceRegister
         * ServiceProvider作为初始化服务提供的参数，里面包含其后两个组件。
         */
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.providerServiceInterface(userService, true);

//        RPCServer rpcServer = new ThreadPoolRPCServerImpl(serviceProvider);
        RPCServer rpcServer = new NettyRPCServerImpl(serviceProvider);
        rpcServer.start(9999);
    }
}
