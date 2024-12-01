package client.rpcClient;

import common.message.RPCRequest;
import common.message.RPCResponse;

public interface RPCClient {
    // 定义底层通信的方式
    RPCResponse sendRequest(RPCRequest rpcRequest);
}
