package main.version1.v3.server;

import main.version1.v3.common.service.UserService;
import main.version1.v3.common.service.impl.UserServiceImpl;
import main.version1.v3.server.provider.ServiceProvider;
import main.version1.v3.server.server.RPCServer;
import main.version1.v3.server.server.impl.NettyRPCServerImpl;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.providerServiceInterface(userService);

//        RPCServer rpcServer = new ThreadPoolRPCServerImpl(serviceProvider);
        RPCServer rpcServer = new NettyRPCServerImpl(serviceProvider);
        rpcServer.start(9999);
    }
}
