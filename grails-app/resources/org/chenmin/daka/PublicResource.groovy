package org.chenmin.daka

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "public", description = "公共服务相关接口")
@Path('/api/public')
class PublicResource {
    @GET
    @Path('/board')
    @ApiOperation(value = "今日看板",response=TodayBoard.class)
    @Produces(MediaType.APPLICATION_JSON)
    String today() {
        def to = DateTool.today()
        def days = TodayBoard.findByYmd(to)
        def json = days as JSON
        return json
    }

    @GET
    @Path('/last')
    @ApiOperation(value = "最新加入用户列表", notes = "取出最后加入的16个用户")
    @Produces(MediaType.APPLICATION_JSON)
    String lastPersonList() {
        def max = 16
        def offset = 0
        def persons = ClockUser.list([max: max, offset: offset,  sort: "id", order: "desc"])
        def ps = []
        persons.each {p ->
            p.openid=''
            p.unionid=''
            ps << p
        }
        def json = [persons: ps,max: max, offset: offset, count: ClockUser.count()] as JSON
        return json
    }

    @GET
    @Path('/count')
    @ApiOperation(value = "用户总数量", notes = "用于分页计算等")
    @Produces(MediaType.APPLICATION_JSON)
    String personCount() {
        return [count: ClockUser.count()] as JSON
    }

    @GET
    @Path('/rich/{max}/{offset}')
    @ApiOperation(value = "最富有用户列表", notes = "分页取出用户列表，累计奖励倒排")
    @Produces(MediaType.APPLICATION_JSON)
    String richPersonList(
            @ApiParam(required = true, value = "最大返回行数",defaultValue = "10")
            @PathParam("max")
                    int max,
            @ApiParam(required = true, value = "起始行数",defaultValue = "0")
            @PathParam("offset")
                    int offset) {
        def persons = ClockUser.list([max: max, offset: offset, sort: "totalReward", order: "desc"])
        def ps = []
        persons.each {p ->
            p.openid=''
            p.unionid=''
            ps << p
        }
        def count = ClockUser.count()
        boolean hasNext = false
        if((offset+max)<count){
            hasNext = true
        }
        def json = [persons: ps,max: max, offset: offset, count: count,hasNext :hasNext] as JSON
        return json
    }
}