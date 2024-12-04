package part1.common.Message;

import lombok.AllArgsConstructor;

/**
 * @version 1.0
 * @create 2024/6/2 22:29
 */
@AllArgsConstructor
public enum MessageType {
    REQUEST((byte) 0),RESPONSE((byte) 1);
    private byte code;
    public byte getCode(){
        return code;
    }
}