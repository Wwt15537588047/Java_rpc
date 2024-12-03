package part1.common.serializer.myCode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import part1.common.Message.MessageType;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;
import part1.common.serializer.mySerializer.Serializer;

/**
 * @version 1.0
 * @create 2024/6/2 22:24
 *   依次按照自定义的消息格式写入，传入的数据为request或者response
 *   需要持有一个serialize器，负责将传入的对象序列化成字节数组
 */
@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {

    private Serializer serializer;

    /**
     * 升级自定义编解码器：
     * 1.魔术：判断是否是无效数据包。（4个字节）
     * 2.版本号：用于支持协议的支持。（1个字节）
     * 3.消息类型：目前有请求、响应（1个字节）
     * 4.序列化算法：JDK\JSON\Protobuf（1个字节）
     * 为了对其填充，在这里添加一个字节的无意义数据，让请求头中的消息保持在16个字节
     * 5.请求序号：为了双工通信，提供异步能力（4个字节）
     * 6.正文长度：用于指明data的长度（4个字节）
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println(msg.getClass());
        // 1.4个字节的魔术
        out.writeBytes(new byte[]{1,2,3,4});
        // 2.版本号，目前设置为1
        out.writeByte(1);
        //3.写入消息类型
        if(msg instanceof RpcRequest){
            out.writeByte(MessageType.REQUEST.getCode());
        }
        else if(msg instanceof RpcResponse){
            out.writeByte(MessageType.RESPONSE.getCode());
        }
        //4.写入序列化方式
        out.writeByte(serializer.getType());
        // 写入对其填充字段目前无意义：
        out.writeByte(0xff);
        // 5.写入请求序号，目前将其全部设置为10
        out.writeInt(10);
        //得到序列化数组
        byte[] serializeBytes = serializer.serialize(msg);
        //byte[] serializeBytes = JSONObject.toJSONBytes(msg);
        //3.写入长度
        out.writeInt(serializeBytes.length);
        //4.写入序列化数组
        out.writeBytes(serializeBytes);
    }
}
