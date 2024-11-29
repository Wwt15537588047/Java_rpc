package main.version2.v1.client.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import main.version2.v1.client.netty.handler.NettyClientHandler;
import main.version2.v1.common.serializer.myCode.MyDecoder;
import main.version2.v1.common.serializer.myCode.MyEncoder;
import main.version2.v1.common.serializer.mySerializer.Impl.JsonSerializer;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        /**
         * 使用自定义编解码器，读数据进行编结码，同时需要注意下面设计
         * 编码时传入序列化器的种类并构造新的序列化，为什么解码的时候不需要传入对应的序列化器，
         * 因为解码时是消息中的「序列化类型得到对应种类的序列化器」，需要明白的是
         * 发送段编码传递消息给接收端，但接收端并不能知道发送端使用何种序列化方式，只有根据消息中的序列化类型才能得到，进一步构造对应序列化器
         */
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}
