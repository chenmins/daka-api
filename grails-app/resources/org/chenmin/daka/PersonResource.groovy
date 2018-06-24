package org.chenmin.daka

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import weixin.popular.util.EmojiUtil

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "person", description = "个人服务相关接口")
@Path('/api/person')
class PersonResource {

    @POST
    @Path('/update')
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "更新个人资料", notes = "openid,nickname,headImg 为必填；unionid有则填写，其余字段无效")
    ClockUser update(ClockUser body) {
        if(!body.openid){
            return
        }
        println "~~~~/api/person/update~~~~~~~~~~"
        println body as JSON
        //type 1
//        EmojiUtil.parseToHtmlHexadecimal
//        //type 2
//        EmojiUtil.parseToHtmlTag
//        //type 3
//        EmojiUtil.parseToAliases
//        //type 4
//        EmojiUtil.parseToHtmlDecimal
//        //type 5
//        EmojiUtil.removeAllEmojis
        //表情包替换
        body.nickname = EmojiUtil.parse(body.nickname,5)
        def has = ClockUser.findByOpenid(body.openid)
        if(has == null){
            def staminaStar = new ClockUser()
            staminaStar.openid = body.openid
            staminaStar.nickname = body.nickname
            staminaStar.headImg = body.headImg
            if(body.unionid)
                staminaStar.unionid = body.unionid
            if(body.popenid!=null )
                staminaStar.popenid = body.popenid
            staminaStar.staminaCount = 0
            staminaStar.paid = 0
            staminaStar.cash = 0
            staminaStar.totalReward = 0
            staminaStar.save(flush: true)
            return ClockUser.findByOpenid(body.openid)
        }else{
            has.nickname = body.nickname
            has.headImg = body.headImg
            if(body.unionid)
                has.unionid = body.unionid
            if(body.popenid!=null && has.popenid==null)
                has.popenid = body.popenid
            has.save(flush: true)
            return ClockUser.findByOpenid(body.openid)
        }
    }

    @GET
    @Path('/board/{openid}')
    @ApiOperation(value = "个人看板")
    @Produces(MediaType.APPLICATION_JSON)
    String person(@ApiParam(required = true, value = "微信个人ID")
                  @PathParam("openid")
                          String openid) {
        println "~~~~/api/board/${openid}~~~~~~~~~~"
        def persons = ClockUser.findByOpenid(openid)
        if(persons==null){
            persons = new ClockUser()
            persons.id = 9999
        }
        def json = persons as JSON
        println json
        return json
    }

    @GET
    @Path('/rewardBoard/{openid}/{yyyyMM}')
    @ApiOperation(value = "个人日历看板", notes = "根据4位年2位月取出指定用户的月份的奖励金列表")
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
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
        def cashs = CashBoard.findAllByOpenid(openid,[max: max, offset: offset, sort: "id", order: "desc"])
        def count = CashBoard.count()
        boolean hasNext = false
        if((offset+max)<count){
            hasNext = true
        }
        def json = [cashs: cashs, max: max, offset: offset, count:count,hasNext:hasNext] as JSON
        return json
    }


}