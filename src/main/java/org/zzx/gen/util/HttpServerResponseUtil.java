package org.zzx.gen.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class HttpServerResponseUtil {

    public static void httpRepose(ChannelHandlerContext ctx,String content, String type){
        ByteBuf buf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);//构建HTTP响应

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, type); // 响应编码
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    public static void reponse(ChannelHandlerContext ctx,Object content){
        JSONObject result = new JSONObject();
        if ( content.equals("success")) {
            result.putOnce("status",200);
            result.putOnce("msg","请求成功");
        } else if ( content.equals("error")){
            result.putOnce("status",500);
            result.putOnce("msg","请求失败");
        } else {
            result.putOnce("status", 200);
            result.putOnce("msg", "请求成功");
            result.putOnce("data",content);
        }
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(JSONUtil.toJsonStr(result).getBytes(Charset.forName("UTF-8"))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
