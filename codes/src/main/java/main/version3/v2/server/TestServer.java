package main.version3.v2.server;

import main.version3.v2.common.service.UserService;
import main.version3.v2.common.service.impl.UserServiceImpl;
import main.version3.v2.server.provider.ServiceProvider;
import main.version3.v2.server.server.RPCServer;
import main.version3.v2.server.server.impl.NettyRPCServerImpl;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.providerServiceInterface(userService, true);

//        RPCServer rpcServer = new ThreadPoolRPCServerImpl(serviceProvider);
        RPCServer rpcServer = new NettyRPCServerImpl(serviceProvider);
        rpcServer.start(9999);
    }
}
