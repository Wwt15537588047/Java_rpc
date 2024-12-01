package common.serializer.myCode;

import common.message.MessageType;
import common.message.RPCRequest;
import common.message.RPCResponse;
import common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder{
//    依次按照自定义的消息格式写入，传入的数据为request或者response
//    需要持有一个serialize器，负责将传入的对象序列化成字节数组
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        log.info("encode 消息类型：{}", msg.getClass());
        // 写入消息类型
        if(msg instanceof RPCRequest){
            out.writeShort(MessageType.REQUEST.getCode());
        }else if (msg instanceof RPCResponse){
            out.writeShort(MessageType.RESPONSE.getCode());
        }
        // 写入序列化方式
        out.writeShort(serializer.getType());
        // 得到序列化数组
        byte[] data = serializer.serialize(msg);
        // 写入长度
        out.writeInt(data.length);
        // 写入序列化数组
        out.writeBytes(data);
    }
}
