package org.chenmin.daka

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "wx", description = "个人微信服务相关接口")
@Path('/api/wx')
class WxResource {

    WxUserService wxUserService

    ClockUserService clockUserService

    @GET
    @Path('/user/{openid}')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据openid获得微信用户资料", notes = "")
    WxUser user(@ApiParam(required = true, value = "微信个人ID")
                @PathParam("openid")
                        String openid) {
        return wxUserService.get(openid)
    }

    @GET
    @Path('/unionid/{unionid}')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据unionid获得打卡用户资料", notes = "")
    WxUser unionid(@ApiParam(required = true, value = "微信个人unionid")
                @PathParam("unionid")
                        String unionid) {
        return clockUserService.getByUnionid(unionid)
    }
}