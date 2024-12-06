package part1.Client.rpcClient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import part1.Client.netty.nettyInitializer.NettyClientInitializer;
import part1.Client.rpcClient.RpcClient;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcRequestSerializer;
import part1.common.Message.RpcResponse;
import part1.Client.serviceCenter.ServiceCenter;
import part1.common.util.RequestTransForm;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClient implements RpcClient {

    private static Bootstrap bootstrap;
    private static EventLoopGroup eventLoopGroup;

    private ServiceCenter serviceCenter;
    public NettyRpcClient(ServiceCenter serviceCenter, int serializerType) throws InterruptedException {
        this.serviceCenter=serviceCenter;
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer(serializerType));
    }

    public NettyRpcClient(ServiceCenter serviceCenter) throws InterruptedException {
        this.serviceCenter=serviceCenter;
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    //netty客户端初始化
//    static {
//        eventLoopGroup = new NioEventLoopGroup();
//        bootstrap = new Bootstrap();
//        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
//                .handler(new NettyClientInitializer());
//    }

    //客户端发生请求
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        log.info("客户端发生请求" + request);
        //从注册中心获取host,post
        InetSocketAddress address = serviceCenter
                .serviceDiscovery(request.getInterfaceName() + "." +request.getReferences().version());
        String host = address.getHostName();
        int port = address.getPort();
        try {
            // channelFuture操作执行结果
            ChannelFuture channelFuture  = bootstrap.connect(host, port).sync();
            // channelFuture的channel()方法获取连接相关连的channel
            Channel channel = channelFuture.channel();
            // 进行请求类型转换，将其替换为不含注解的类
            RpcRequestSerializer requestSerializer = RequestTransForm.RequestTo(request);
            // 发送数据
            channel.writeAndFlush(requestSerializer);
            //sync()堵塞获取结果
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在handler中设置）
            // AttributeKey是，线程隔离的，不会有线程安全问题。
            // 当前场景下选择堵塞获取结果
            // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();

            log.info("完成请求——请求返回结果（客户端接受）" + response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
