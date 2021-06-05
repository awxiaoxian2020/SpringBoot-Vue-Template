package com.example.web.controller.net;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 基本数据包，默认成功状态为false
 */

@ApiModel(value = "ResponsePacket", description = "处理结果JSON数据包")
@Getter
@Setter
public class ResponsePacket {
    private boolean success;
    private String message = "";

    public ResponsePacket(){ }

    public ResponsePacket(String def){
        this.message = def;
    }

    /**
     * 设置消息
     * @param message 消息
     */
    public void msg(String message){
        this.message = message;
    }

    /**
     * 切换为成功状态
     */
    public void success(){
        this.success = true;
    }

    /**
     * 转换为JSON数据包
     * @return json
     */
    protected JSONObject asJSON(){
        JSONObject object = new JSONObject();
        object.put("success", this.success);
        object.put("message", this.message);
        return object;
    }

    @Override
    public String toString() {
        return this.asJSON().toString();
    }
}
