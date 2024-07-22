package main.version4.v1.server.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.version4.v1.common.message.RPCRequest;
import main.version4.v1.common.message.RPCResponse;
import main.version4.v1.server.provider.ServiceProvider;
import main.version4.v1.server.rateLimit.RateLimit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private ServiceProvider serviceProvider;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest request) throws Exception {
        //接收request，读取并调用服务
        RPCResponse response = getResponse(request);
        ctx.writeAndFlush(response);
        ctx.close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    private RPCResponse getResponse(RPCRequest rpcRequest){
        //得到服务名
        String interfaceName=rpcRequest.getInterfaceName();
        // 接口限流降级
        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(rpcRequest.getInterfaceName());
        if(!rateLimit.getToken()){
            return RPCResponse.fail();
        }
        //得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        //反射调用方法
        Method method=null;
        try {
            method= service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object invoke=method.invoke(service,rpcRequest.getParams());
            return RPCResponse.sussess(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RPCResponse.fail();
        }
    }
}
