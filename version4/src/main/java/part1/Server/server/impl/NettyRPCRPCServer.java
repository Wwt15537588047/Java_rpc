package part1.Server.server.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import part1.Server.netty.nettyInitializer.NettyServerInitializer;
import part1.Server.provider.ServiceProvider;
import part1.Server.record.ServiceRecord;
import part1.Server.server.RpcServer;

/**
 * @version 1.0
 * @create 2024/2/26 14:01
 */
@AllArgsConstructor
@Slf4j
public class NettyRPCRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;
    private int serializerType;

    @Override
    public void start(int port) {
        // netty 服务线程组boss负责建立连接， work负责具体的请求，此处使用的是主从多线程模型。「当bossGroup的NioEventLoopGroup设置为1时表示使用的是多线程模型」
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
//        log.info("netty服务端启动了");
        try {
            //创建服务端启动引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //给引导类配置线程组，确定线程模型；给引导类指定IO模型为NIO模型；给引导类创建NettyServerInitializer（实现了ChannelInitializer类），指定服务端的业务处理逻辑。
            serverBootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                    //将服务提供者传递 进行netty服务初始化，然后在channel中进行监听
                    .childHandler(new NettyServerInitializer(serviceProvider, serializerType));
            //同步堵塞
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("netty服务端启动了,端口号为：{}", port);
            //阻塞等待直到服务器的Channel关闭,「closeFuture方法获取Channel的CloseFuture对象，然后调用sync方法」
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}