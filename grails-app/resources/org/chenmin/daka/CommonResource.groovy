package org.chenmin.daka

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.http.client.HttpClient

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "common", description = "常规服务相关接口")
@Path('/api/common')
class CommonResource {

    boolean verify = false

    @GET
    @Path('/ip')
    @ApiOperation(value = "获得服务器请求IP", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String ip(){
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient()
        def url = "http://httpbin.org/ip"
        //http://ip.taobao.com/service/getIpInfo.php?ip=myip
        //url = "http://ip.taobao.com/service/getIpInfo.php?ip=myip"
        def p = new HashMap<String,String>()
        def json = HttpClientTools.get(hc,url,p)
        chc.close()
        return json
    }

    @GET
    @Path('/time')
    @ApiOperation(value = "获得服务器当前时间", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String time(){
        def t =[:]
        t.today = DateTool.today()
        t.time=DateTool.time()
        return t as JSON
    }

    @GET
    @Path('/verify/status')
    @ApiOperation(value = "获得审核状态", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String verifyStatus(){
        def t =[:]
        t.verify = verify
        t.time=DateTool.time()
        return t as JSON
    }

    @GET
    @Path('/verify/reverse')
    @ApiOperation(value = "反转并返回审核状态", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String verify(){
        if(verify)
            verify = false
        else
            verify = true
        def t =[:]
        t.verify = verify
        t.time=DateTool.time()
        return t as JSON
    }



    @POST
    @Path('/sign')
    @Produces('text/plain')
    @ApiOperation(value = "jwt签名", notes = "jwt签名123")
    String sign(@ApiParam(required = true, value = "报文") String body) {
        println "body:${body}"
        String token = JWT.sign(body, 60L * 1000L * 30L);
        return token
    }

    @POST
    @Path('/unsign')
    @Produces('text/plain')
    @ApiOperation(value = "jwt签名解码")
    String unsign(@ApiParam(required = true, value = "报文") String body) {
        println "unsign:body:${body}"
        String token = JWT.unsign(body, String.class);
        println "token:${token}"
        return token
    }


}