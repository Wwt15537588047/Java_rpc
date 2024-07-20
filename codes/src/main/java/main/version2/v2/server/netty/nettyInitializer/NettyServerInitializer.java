package main.version2.v2.server.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.version2.v2.common.serializer.myCode.MyDecoder;
import main.version2.v2.common.serializer.myCode.MyEncoder;
import main.version2.v2.common.serializer.mySerializer.Impl.JsonSerializer;
import main.version2.v2.server.netty.handler.NettyRPCServerHandler;
import main.version2.v2.server.provider.ServiceProvider;

@Slf4j
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 使用自定的编解码器
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
