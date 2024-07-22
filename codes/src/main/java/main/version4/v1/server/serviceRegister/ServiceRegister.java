package main.version4.v1.server.serviceRegister;

import java.net.InetSocketAddress;

// 服务注册接口
public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serviceAddress, boolean canRetry);
}
