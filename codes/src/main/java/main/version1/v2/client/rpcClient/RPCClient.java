package main.version1.v2.client.rpcClient;

import main.version1.v2.common.message.RPCRequest;
import main.version1.v2.common.message.RPCResponse;

public interface RPCClient {
    /**
     * 定义底层通信方式，对于简单网络传输和使用高性能Netty库均走此路由
     * 客户端统一的接口，随后对于客户端实现的各种通信方式，比如Socket，Netty只需要实现此接口就可以实现复用
     * ----在ClientProxy中用接口定义一个传输类属性，可以灵活选择不同方式的传输类，降低耦合度。----
      */
    RPCResponse sendRequest(RPCRequest rpcRequest);
}
