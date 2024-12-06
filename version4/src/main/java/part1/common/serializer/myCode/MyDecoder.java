package part1.common.serializer.myCode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import part1.common.Message.MessageType;
import part1.common.serializer.mySerializer.Serializer;

import java.util.List;

/**
 * @version 1.0
 * @create 2024/6/2 22:24
 * 按照自定义的消息格式解码数据
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        // 1.读取4个字节的魔术
        int magicNum = in.readInt();
        // 2.读取1个字节的版本号
        byte version = in.readByte();
        // 3.读取消息类型
        byte messageType = in.readByte();
        // 现在还只支持request与response请求
        if(messageType != MessageType.REQUEST.getCode() &&
                messageType != MessageType.RESPONSE.getCode()){
            System.out.println("暂不支持此种数据");
            return;
        }
        //4.读取序列化的方式&类型
        byte serializerType = in.readByte();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if(serializer == null)
            throw new RuntimeException("不存在对应的序列化器");
        // 读取填充字段，当前无意义
        in.readByte();
        // 5.读取请求序号
        int sequenceId = in.readInt();
        // 6.读取序列化数组长度
        int length = in.readInt();
        //4.读取序列化数组
        byte[] bytes=new byte[length];
        in.readBytes(bytes);
//        System.out.println("bytes==="+new String(bytes));
        Object deserialize= serializer.deserialize(bytes, messageType);
        log.info("接收方正在进行消息反序列化，序列化方式为：{},反序列化后的消息为：{}", Serializer.getSerializerNameByCode(serializer.getType()), deserialize);
//        log.info("当前反序列化类型为 serializerType : {}, 反序列化后的消息为：{}", Serializer.getSerializerNameByCode(serializerType), deserialize);
        log.info("解码器当前读取到的消息，magicNum:{}, version:{}, messageType:{},serializerType:{}, sequenceId:{}", magicNum, version,messageType,serializerType,sequenceId);
        out.add(deserialize);
    }
}
