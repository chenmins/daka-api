package org.chenmin.daka

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Api(value = "person", description = "个人服务相关接口")
@Path('/api/person')
class PersonResource {

    @GET
    @Path('/board/{openid}')
    @ApiOperation(value = "个人看板")
    @Produces('text/plain')
    String person(@ApiParam(required = true, value = "微信个人ID")
                  @PathParam("openid")
                          String openid) {
        def persons = ClockUser.findByOpenid(openid)
        def json = persons as JSON
        return json
    }

}