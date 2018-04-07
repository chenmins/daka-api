package org.chenmin.daka

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Api(value = "common", description = "公共服务相关接口")
@Path('/api/test')
class TestResource {

    @POST
    @Path('/sign')
    @Produces('text/plain')
    @ApiOperation(value="jwt签名", notes="jwt签名123" )
    String sign(@ApiParam(required = true, value = "报文") String body){
        println "body:${body}"
        String token = JWT.sign(body, 60L* 1000L* 30L);
        return token
    }

    @POST
    @Path('/unsign')
    @Produces('text/plain')
    @ApiOperation(value="jwt签名解码" )
    String unsign(@ApiParam(required = true, value = "报文") String body){
        println "unsign:body:${body}"
        String token = JWT.unsign(body,String.class);
        println "token:${token}"
        return token
    }
    @GET
    @Path('/today')
    @ApiOperation(value="今日看板" )
    @Produces('text/plain')
    String today(){
        def to = DateTool.today()
        def days = TodayBoard.findByYmd(to)
        def json = days as JSON
        return json
    }

    @GET
    @Path('/person/{openid}')
    @ApiOperation(value="个人看板" )
    @Produces('text/plain')
    String person(@ApiParam(required = true, value = "微信个人ID")
                  @PathParam("openid")
                          String openid){
        def persons = ClockUser.findByOpenid(openid)
        def json = persons as JSON
        return json
    }
}