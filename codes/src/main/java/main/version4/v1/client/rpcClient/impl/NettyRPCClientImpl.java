package main.version4.v1.client.rpcClient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import main.version4.v1.client.netty.nettyInitializer.NettyClientInitializer;
import main.version4.v1.client.rpcClient.RPCClient;
import main.version4.v1.client.serviceCenter.ServiceCenter;
import main.version4.v1.client.serviceCenter.impl.ZKServiceCenterImpl;
import main.version4.v1.common.message.RPCRequest;
import main.version4.v1.common.message.RPCResponse;

import java.net.InetSocketAddress;

public class NettyRPCClientImpl implements RPCClient {
//    private String host;
//    private int port;
    private ServiceCenter serviceCenter;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    public NettyRPCClientImpl(){
//        this.host=host;
//        this.port=port;
        this.serviceCenter = new ZKServiceCenterImpl();
    }
    public NettyRPCClientImpl(ServiceCenter serviceCenter){
        this.serviceCenter = serviceCenter;
    }
    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                //NettyClientInitializer这里 配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        // 从注册中心获取host, port
        InetSocketAddress inetSocketAddress = serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host = inetSocketAddress.getHostName();
        int port = inetSocketAddress.getPort();
        try {
            //创建一个channelFuture对象，代表这一个操作事件，sync方法表示堵塞直到connect完成
            ChannelFuture channelFuture  = bootstrap.connect(host, port).sync();
            //channel表示一个连接的单位，类似socket
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            //sync()堵塞获取结果
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 当前场景下选择堵塞获取结果
            // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            RPCResponse response = channel.attr(key).get();

            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}