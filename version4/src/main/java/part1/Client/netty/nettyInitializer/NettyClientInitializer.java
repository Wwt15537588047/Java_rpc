package part1.Client.netty.nettyInitializer;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import part1.Client.netty.handler.NettyClientHandler;
import part1.common.serializer.myCode.MyDecoder;
import part1.common.serializer.myCode.MyEncoder;
import part1.common.serializer.mySerializer.Serializer;
import part1.common.serializer.mySerializer.impl.ProtostuffSerializer;


@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private int serializerType;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //使用自定义的编/解码器
        pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(serializerType)));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyClientHandler());
    }
}
