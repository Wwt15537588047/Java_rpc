package main.version4.v1.client.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import main.version4.v1.client.netty.handler.NettyClientHandler;
import main.version4.v1.common.serializer.myCode.MyDecoder;
import main.version4.v1.common.serializer.myCode.MyEncoder;
import main.version4.v1.common.serializer.mySerializer.Impl.JsonSerializer;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 使用自定的编解码器
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}
