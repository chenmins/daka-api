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

@Api(value = "security", description = "个人安全服务相关接口")
@Path('/api/security')
class SecurityResource {
    //支付测试
    @GET
    @Path('/pay/{openid}/{cash}')
    @ApiOperation(value = "支付挑战金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String pay(@ApiParam(required = true, value = "微信个人ID")
                       @PathParam("openid")
                               String openid,
                       @ApiParam(required = true, value = "充值金额（单位分）")
                       @PathParam("cash")
                               int cash) {
        //记录交易流水
        //增加流水数据
        def earlyStar = ClockUser.findByOpenid(openid)
        def cb1 = new CashBoard()
        cb1.user = earlyStar
        cb1.openid = earlyStar.openid
        cb1.cashType = "deposit"
        /**
         * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金）
         */
        cb1.cash = cash
        cb1.remark = "测试付押金10元"
        cb1.save()
        //修改用户押金
        earlyStar.paid = earlyStar.paid + cash
        earlyStar.save()
        //返回用户对象
        def json =  earlyStar as JSON
        return json
    }

    //打卡测试
    @GET
    @Path('/clock/{openid}')
    @ApiOperation(value = "打卡测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String clock(@ApiParam(required = true, value = "微信个人ID")
                 @PathParam("openid")
                         String openid) {
        //插入打卡记录

    }
    //结算测试

    //每日生成

    //提现测试

}