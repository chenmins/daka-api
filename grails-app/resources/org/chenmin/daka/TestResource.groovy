package org.chenmin.daka

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Api(value = "common", description = "公共服务相关接口")
@Path('/api/test')
class TestResource {

    @GET
    @Produces('text/plain')
    @ApiOperation(value="测试cookies", notes="测试cookies值")
    String getTestRepresentation() {
        'Test'
    }

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
}