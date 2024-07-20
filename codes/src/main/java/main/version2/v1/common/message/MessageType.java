package main.version2.v1.common.message;

import lombok.AllArgsConstructor;

// 自定义消息类型枚举
@AllArgsConstructor
public enum MessageType {

    REQUEST(0),RESPONSE(1);
    private int code;
    public int getCode(){
        return code;
    }
}
