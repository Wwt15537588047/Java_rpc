package main.version4.v1.common.serializer.myCode;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import main.version4.v1.common.message.MessageType;
import main.version4.v1.common.serializer.mySerializer.Serializer;

import java.util.List;

@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        log.info("开始进行decode...");
        // 读取消息类型
        short messageType = in.readShort();
        if(messageType != MessageType.REQUEST.getCode() && messageType != MessageType.RESPONSE.getCode()){
            log.info("暂不支持此种数据...");
            return;
        }
        // 读取序列化类型
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if(serializer == null){
            log.info("不存在对应的序列化器...");
            return;
        }
        // 读取序列化数组的长度
        int length = in.readInt();
        // 读取序列化数组
        byte[] data = new byte[length];
        in.readBytes(data);
        Object obj = serializer.deSerialize(data, messageType);
        out.add(obj);
    }
}
