package org.chenmin.daka

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.http.client.HttpClient

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "security", description = "个人安全服务相关接口")
@Path('/api/security')
class SecurityResource {


    @GET
    @Path('/ip')
    @ApiOperation(value = "获得服务器请求IP", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String ip(){
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient()
        def url = "http://httpbin.org/ip"
        def p = new HashMap<String,String>()
        def json = HttpClientTools.get(hc,url,p)
        chc.close()
        return json
    }

    //openid授权
    @GET
    @Path('/jscode2session/{jscode}')
    @ApiOperation(value = "openid授权", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String jscode2session(
            @ApiParam(required = true, value = " 临时登录凭证code")
            @PathParam("jscode")
                    String jscode
    ){
        //https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        def url = "https://api.weixin.qq.com/sns/jscode2session?"
        url += "appid=wxbd7ee929512fd71f"
        url += "&secret=74492633a33a639fa1301c2ae4310446"
        url += ("&js_code="+jscode)
        url += "&grant_type=authorization_code"
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient();
        def p = new HashMap<String,String>()
        def json = HttpClientTools.get(hc,url,p)
        chc.close()
        return json
    }
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
    @GET
    @Path('/take/{openid}/{cash}')
    @ApiOperation(value = "提取奖励金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String take(@ApiParam(required = true, value = "微信个人ID")
               @PathParam("openid")
                       String openid,
               @ApiParam(required = true, value = "提取金额（单位分）")
               @PathParam("cash")
                       int cash) {

    }


    @GET
    @Path('/refund/{openid}/{cash}')
    @ApiOperation(value = "退还挑战金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String refund(@ApiParam(required = true, value = "微信个人ID")
                @PathParam("openid")
                        String openid,
                @ApiParam(required = true, value = "提取金额（单位分）")
                @PathParam("cash")
                        int cash) {

    }

}

import javax.ws.rs.core.MediaType
