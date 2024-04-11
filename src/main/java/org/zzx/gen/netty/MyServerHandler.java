package org.zzx.gen.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import org.zzx.gen.entity.DbInfo;
import org.zzx.gen.entity.TableInfo;
import org.zzx.gen.service.GenService;
import org.zzx.gen.util.HTLMContentUtil;
import org.zzx.gen.util.HttpServerResponseUtil;
import org.zzx.gen.util.RequestParamUtil;

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

    private void service(ChannelHandlerContext ctx ) throws Exception {
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
            case "/testConnection":
                log.info("测试数据库连接");
                testConService(ctx);
                break;
            case "/genSqlTable":
                log.info("通过模板生成sql表");
                // 解析参数
                genSqlTableService(ctx);
                break;
            case "/execuSqlInDb":
                log.info("将生成的sql在数据库中运行");
                // 解析参数
                execuSqlInDbService(ctx);
                break;
            case "/getDbInfo":
                log.info("获取数据库信息");
                HttpServerResponseUtil.reponse(ctx, GenService.getDBInfo());
                break;
            default:
                log.info("default");
                HttpServerResponseUtil.reponse(ctx, "success");
                break;
        }
    }

    private void execuSqlInDbService(ChannelHandlerContext ctx) throws Exception {
        GenService genService = GenService.getInstance();
        genService.execuSqlInDb();
        HttpServerResponseUtil.reponse(ctx, "success");
    }

    private void genSqlTableService(ChannelHandlerContext ctx) throws Exception {
        RequestParamUtil param = new RequestParamUtil(request, httpContent);
        GenService genService = GenService.getInstance();
        TableInfo tableInfo = JSONUtil.toBean(param.getParamsJson(), TableInfo.class);
        String genResult = genService.gen(tableInfo, "table.sql.ftl");
        HttpServerResponseUtil.reponse(ctx, genResult);
    }

    private void testConService(ChannelHandlerContext ctx) {
        GenService genService = GenService.getInstance();

        DbInfo dbInfo = new DbInfo();
        RequestParamUtil conParam = new RequestParamUtil(request, httpContent);
        JSONObject paramsJson = conParam.getParamsJson();

        dbInfo.setDriver(paramsJson.getStr("driver"));
        dbInfo.setUrl(paramsJson.getStr("url"));
        dbInfo.setUserName(paramsJson.getStr("userName"));
        dbInfo.setPassword(paramsJson.getStr("password"));

        Boolean testResult = genService.testConnection(dbInfo);
        if (testResult){
            HttpServerResponseUtil.reponse(ctx, "success");
        } else {
            HttpServerResponseUtil.reponse(ctx, "error");
        }
    }

    private String requestUrlHandler(String requestUrl) {
        String resultUrl = requestUrl;
        if (requestUrl.contains("/api")) {
            resultUrl =  requestUrl.substring(requestUrl.indexOf("/api") + 4);
        }
        if (resultUrl.contains("?")) {
            return resultUrl.substring(0, requestUrl.indexOf("?"));
        }
        if (resultUrl.contains(".js") || resultUrl.contains(".css")) {
            String url = resultUrl.split("/")[1];
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
