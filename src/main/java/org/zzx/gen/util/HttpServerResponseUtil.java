package org.zzx.gen.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

public class HttpServerResponseUtil {
    public static void reponse(ChannelHandlerContext ctx,String content){

        String sendSucessMsg =  "{\n" +
                " \"status\": 200,\n" +
                " \"msg\": \"请求成功\"\n" +
                "}";

        String sendErrorMsg =  "{\n" +
                " \"status\": 500,\n" +
                " \"msg\": \"请求失败\"\n" +
                "}";
        String sendResult = content.equals("success")? sendSucessMsg: sendErrorMsg;

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(sendResult.getBytes(Charset.forName("UTF-8"))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
