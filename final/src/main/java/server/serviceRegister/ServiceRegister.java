package server.serviceRegister;

import server.integration.RpcService;

import java.net.InetSocketAddress;

// 服务注册接口
public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry);

    void register(String serviceName, InetSocketAddress serviceAddress, RpcService rpcService);
}
