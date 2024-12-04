package part1.common.util;

import part1.Server.integration.impl.ReferencesImpl;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcRequestSerializer;

/**
 * @Author wt
 * @Description RpcRequest转换，消除reference注解类型
 * @Data 2024/12/4 上午10:47
 */
public class RequestTransForm {

    public static RpcRequestSerializer RequestTo(RpcRequest rpcRequest){
        RpcRequestSerializer requestSerializer = new RpcRequestSerializer();
        requestSerializer.setGroup(rpcRequest.getReferences().group());
        requestSerializer.setVersion(rpcRequest.getReferences().version());
        requestSerializer.setLoadBalance(rpcRequest.getReferences().loadBalance());
        requestSerializer.setInterfaceName(rpcRequest.getInterfaceName());
        requestSerializer.setMethodName(rpcRequest.getMethodName());
        requestSerializer.setParamsType(rpcRequest.getParamsType());
        requestSerializer.setParams(rpcRequest.getParams());
        return requestSerializer;
    }

    public static RpcRequest GetRequest(RpcRequestSerializer rpcRequestSerializer){
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(rpcRequestSerializer.getInterfaceName());
        rpcRequest.setMethodName(rpcRequestSerializer.getMethodName());
        rpcRequest.setParamsType(rpcRequestSerializer.getParamsType());
        rpcRequest.setParams(rpcRequestSerializer.getParams());

        // 构造注解
        rpcRequest.setReferences(new ReferencesImpl(rpcRequestSerializer.getVersion(), rpcRequestSerializer.getGroup(), rpcRequestSerializer.getLoadBalance()));
//        rpcRequest.getReferences().version() = rpcRequestSerializer.getLoadBalance();
//        rpcRequest.setReferences().version(rpcRequestSerializer.getVersion());
//        rpcRequest.getReferences().group();
//        rpcRequest.getReferences().loadBalance()
        return rpcRequest;
    }
}
