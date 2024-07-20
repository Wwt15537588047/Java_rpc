package main.version2.v2.client.rpcClient;

import main.version2.v2.common.message.RPCRequest;
import main.version2.v2.common.message.RPCResponse;

public interface RPCClient {
    // 定义底层通信的方式
    RPCResponse sendRequest(RPCRequest rpcRequest);
}
