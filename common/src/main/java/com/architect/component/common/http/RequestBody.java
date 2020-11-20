package com.architect.component.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    //表单提交Type application/x-www-form-urlencoded
    public static final String TYPE = "application/x-www-form-urlencoded";

    Map<String, String> bodys = new HashMap<>();

    public void addBody(String key, String value) {
        try {
            bodys.put(URLEncoder.encode(key, "utf-8"), URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getBody() {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> entry : bodys.entrySet()) {
            // name=张三&age=16
            buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        //删除最后一个&
        if (buffer.length() != 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
}
