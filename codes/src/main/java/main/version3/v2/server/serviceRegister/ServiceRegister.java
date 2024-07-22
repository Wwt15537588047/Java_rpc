package main.version3.v2.server.serviceRegister;

import java.net.InetSocketAddress;

// 服务注册接口
public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry);
}