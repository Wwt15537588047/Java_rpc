package main.version1.v2.client.rpcClient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import main.version1.v2.client.netty.nettyInitializer.NettyClientInitializer;
import main.version1.v2.client.rpcClient.RPCClient;
import main.version1.v2.common.message.RPCRequest;
import main.version1.v2.common.message.RPCResponse;

/**
 * 传输类Netty实现客户端接口，这个类的作用仅仅构建Netty，客户端Netty的具体执行在
 * NettyClientInitializer和NettyClientHandler中，前者对发送消息进行编码，并发送，此外对接收到的消息解码，并发送给后者进行处理
 */
public class NettyRPCClientImpl implements RPCClient {
    private String host;
    private int port;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    public NettyRPCClientImpl(String host,int port){
        this.host=host;
        this.port=port;
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
        try {
            //创建一个channelFuture对象，代表这一个操作事件，sync方法表示堵塞直到connect完成
            ChannelFuture channelFuture  = bootstrap.connect(host, port).sync();
            //channel表示一个连接的单位，类似socket
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            //sync()堵塞获取结果
            channel.closeFuture().sync();
            /**
             * 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在handler中设置）
             * AttributeKey是，线程隔离的，不会有线程安全问题。
             * 当前场景下选择堵塞获取结果
             * 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
             */
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