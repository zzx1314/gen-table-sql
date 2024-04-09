package org.zzx.gen.netty;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import org.zzx.gen.util.HttpServerResponseUtil;
import org.zzx.gen.util.RequestParamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyServerHandler extends SimpleChannelInboundHandler<Object> {
    Log log = LogFactory.get();

    private HttpRequest request;

    private HttpContent httpContent;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            this.request = (DefaultHttpRequest) msg;
        }
        if (msg instanceof HttpContent) {
            this.httpContent = (HttpContent) msg;

            this.service(ctx);
        }
    }

    private void service(ChannelHandlerContext ctx ) {
        switch (requestUrlHandler(request.uri())){
            case "/updateFlinkConfig":
                log.info("handleFlinkConfig");
                // 解析参数
                RequestParamUtil param = new RequestParamUtil(request, httpContent);
                HttpServerResponseUtil.reponse(ctx, "error");
                break;
            default:
                log.info("default");
                HttpServerResponseUtil.reponse(ctx, "success");
                break;
        }
    }

    private String requestUrlHandler(String requestUrl) {
        if (requestUrl.contains("?")) {
            return requestUrl.substring(0, requestUrl.indexOf("?"));
        }
        return requestUrl;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

}
