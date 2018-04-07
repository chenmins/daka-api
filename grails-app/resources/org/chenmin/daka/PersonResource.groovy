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

    @GET
    @Path('/rewardBoard/{openid}/{yyyyMM}')
    @ApiOperation(value = "个人日历看板", notes = "根据4位年2位月取出指定用户的月份的奖励金列表")
    @Produces('text/plain')
    String rewardBoard(@ApiParam(required = true, value = "微信个人ID")
                       @PathParam("openid")
                               String openid,
                       @ApiParam(required = true, value = "4位年2位月")
                       @PathParam("yyyyMM")
                               String yyyyMM) {
        def json = RewardBoard.findAllByOpenidAndYm(openid, yyyyMM) as JSON
        return json
    }

    @GET
    @Path('/cash/{openid}/{max}/{offset}')
    @ApiOperation(value = "现金流水列表", notes = "分页取出用户现金流水，按时间倒排")
    @Produces('text/plain')
    String cashList(
            @ApiParam(required = true, value = "微信个人ID")
            @PathParam("openid")
                    String openid,
            @ApiParam(required = true, value = "最大返回行数", defaultValue = "10")
            @PathParam("max")
                    int max,
            @ApiParam(required = true, value = "起始行数", defaultValue = "0")
            @PathParam("offset")
                    int offset) {
        def cashs = CashBoard.list([max: max, offset: offset, sort: "id", order: "desc"])
        def json = [cashs: cashs, max: max, offset: offset, count: CashBoard.count()] as JSON
        return json
    }


}