package main.version1.v1.server;

import main.version1.v1.common.service.impl.UserServiceImpl;
import main.version1.v1.server.server.impl.SimpleRPCServerImpl;
import main.version1.v1.server.server.provider.ServiceProvider;

public class TestServer {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.providerServiceInterface(userService);

        SimpleRPCServerImpl simpleRPCServer = new SimpleRPCServerImpl(serviceProvider);
        simpleRPCServer.start(9999);
    }
}
