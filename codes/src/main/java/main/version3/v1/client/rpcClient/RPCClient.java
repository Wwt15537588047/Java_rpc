package main.version3.v1.client.rpcClient;

import main.version3.v1.common.message.RPCRequest;
import main.version3.v1.common.message.RPCResponse;

public interface RPCClient {
    // 定义底层通信的方式
    RPCResponse sendRequest(RPCRequest rpcRequest);
}
