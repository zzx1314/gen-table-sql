package org.zzx.gen.netty;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import org.zzx.gen.util.HTLMContentUtil;
import org.zzx.gen.util.HttpServerResponseUtil;
import org.zzx.gen.util.RequestParamUtil;


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
            case "assets":
                log.info("请求静态资源");
                String staticContex = HTLMContentUtil.load("static/dist/" +request.uri());
                if (request.uri().contains(".js")){
                    HttpServerResponseUtil.httpRepose(ctx, staticContex, "application/javascript;charset=UTF-8");
                } else if (request.uri().contains(".css")){
                    HttpServerResponseUtil.httpRepose(ctx, staticContex, "text/css;charset=UTF-8");
                }
                break;
            case "/index":
                log.info("请求前端路径");
                String contex = HTLMContentUtil.load("static/dist/index.html");
                HttpServerResponseUtil.httpRepose(ctx, contex, "text/html;charset=UTF-8");
                break;
            case "/setDbConfig":
                log.info("setDbConfig---");
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
        if (requestUrl.contains(".js") || requestUrl.contains(".css")) {
            String url = requestUrl.split("/")[1];
            return url;
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
