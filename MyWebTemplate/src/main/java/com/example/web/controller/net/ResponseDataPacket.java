package com.example.web.controller.net;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * 包含Data数据的数据包。
 */
@ApiModel(value = "ResponseDataPacket", description = "处理结果JSON数据包（带数据）")
@Getter
@Setter
public class ResponseDataPacket extends ResponsePacket{
    private final JSONObject data = new JSONObject();

    public void modifyData(Consumer<JSONObject> consumer){
        consumer.accept(this.data);
    }

    @Override
    public JSONObject asJSON() {
        JSONObject object = super.asJSON();
        object.put("data", data);
        return object;
    }
}
