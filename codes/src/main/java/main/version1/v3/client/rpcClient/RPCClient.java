package main.version1.v3.client.rpcClient;

import main.version1.v3.common.message.RPCRequest;
import main.version1.v3.common.message.RPCResponse;

public interface RPCClient {
    // 定义底层通信的方式
    RPCResponse sendRequest(RPCRequest rpcRequest);
}
