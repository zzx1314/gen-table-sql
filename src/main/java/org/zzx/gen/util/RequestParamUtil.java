package org.zzx.gen.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现get和post请求参数的接收
 */
public class RequestParamUtil {
    private HttpRequest request;

    private HttpContent content;

    private Map<String, List<String>> params = new HashMap<>();

    private JSONObject paramsJson = new JSONObject();

    public RequestParamUtil(HttpRequest request,HttpContent content, ByteBuf buf) {
        this.request = request;
        this.content = content;
        this.parse(buf);
    }

    private void parse(ByteBuf buf) {
        if (HttpMethod.GET == request.method()) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            this.params.putAll(decoder.parameters());
        } else if (HttpMethod.POST == request.method() ){
            // 通过不同请求头解析post请求
            DefaultHttpRequest defaultHttpRequest = (DefaultHttpRequest) request;
            HttpHeaders headers = defaultHttpRequest.headers();
            String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
            if ("application/x-www-form-urlencoded".equals(contentType)){
                handlerForm();
            } else if ("application/json".equals(contentType)){
                handlerJson(buf);
            } else {
                // 其他格式的参数
            }
        }
    }

    /**
     * 处理json数据
     */
    private void handlerJson(ByteBuf byteData) {
        if (!(byteData instanceof EmptyByteBuf)) {
            //接收msg消息
            byte[] msgByte = new byte[byteData.readableBytes()];
            byteData.readBytes(msgByte);

            String parm = new String(msgByte, Charset.forName("UTF-8"));
            System.out.println(parm);
            JSONObject jsonObject = JSONUtil.parseObj(parm);
            paramsJson = jsonObject;
        }
    }

    /**
     * 处理form表单数据
     */
    private void handlerForm() {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        decoder.offer(content);
        List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
        for (InterfaceHttpData bodyHttpData : bodyHttpDatas){
            try {
                Attribute attribute = (Attribute) bodyHttpData;
                List<String> values = null;
                if (this.params.containsKey(attribute.getName())) {
                    values = this.params.get(attribute.getName());
                } else {
                    values = new ArrayList<>();
                }
                values.add(attribute.getValue());
                this.params.put(attribute.getName(), values);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取单个参数
     */
    public String getParamOne(String paramName){
        List<String> values = this.params.get(paramName);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    /**
     * 获取所有的values
     */
    public List<String> getParamValues(String paramName){
        return this.params.get(paramName);
    }

    /**
     * 获取所有参数
     */
    public  Map<String, List<String>> getParams(){
        return this.params;
    }

    public JSONObject getParamsJson(){
        return this.paramsJson;
    }
}
