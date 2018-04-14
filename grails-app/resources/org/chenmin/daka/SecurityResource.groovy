package org.chenmin.daka

import grails.converters.JSON
import groovy.sql.Sql
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

    def dataSource

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
        cb1.remark = "测试付押金"+(cash/100)+"元"
        cb1.refund = "-1"
        //TODO 改为真是的
        cb1.orderID= UUIDTool.getUUID()
        cb1.save(flush: true)
        //修改用户押金
        earlyStar.paid = earlyStar.paid + cash
        earlyStar.save(flush: true)
        //TODO 更新挑战金
        int spaid = 0
        def sql = new Sql(dataSource);
        String strSql = "select sum(paid) spaid from daka_clock_user t ";
        sql.eachRow(strSql) {
            spaid = it.spaid
        }
        def hasToday = TodayBoard.findByYmd(DateTool.today())
        hasToday.currentTotalMoney = spaid
        hasToday.save(flush: true)
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
        //检测打卡记录
        def has = RewardBoard.findByOpenidAndYmd(openid,DateTool.today())
        def earlyStar = ClockUser.findByOpenid(openid)
        if(!has){
            //插入打卡记录
            def r1 = new RewardBoard()
            r1.user = earlyStar
            r1.openid = earlyStar.openid
            r1.ymd = DateTool.today()
            r1.ym = DateTool.month()
            r1.d =DateTool.d()
            r1.reward = -1
            r1.hitTime = DateTool.time()
            r1.hitType = "wx"
            r1.save(flush: true)
            //修改今日记录
            earlyStar.todayTime = DateTool.time()
            earlyStar.staminaCount = earlyStar.staminaCount+1
            earlyStar.save(flush: true)
        }
        return earlyStar as JSON
    }

    //结算测试
    @GET
    @Path('/calc/{cash}')
    @ApiOperation(value = "提取奖励金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String calc(
                @ApiParam(required = true, value = "预留金额（单位分）")
                @PathParam("cash")
                        int cash) {
        //罚没未打卡的挑战金
        //扣除预留
        //算出费率
        //发放奖励
        //平差价（四舍五入）
    }

    //未退的挑战金列表
    @GET
    @Path('/payList/{openid}')
    @ApiOperation(value = "未退的押金列表", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    List<CashBoard> payList(@ApiParam(required = true, value = "微信个人ID")
                   @PathParam("openid")
                           String openid){
        return null
    }

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
        //记录资金流水日志

        //扣除奖励金

    }

    //退还挑战金测试
    @GET
    @Path('/refund/{openid}/{orderID}')
    @ApiOperation(value = "退还挑战金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String refund(@ApiParam(required = true, value = "微信个人ID")
                @PathParam("openid")
                        String openid,
                @ApiParam(required = true, value = "充值")
                @PathParam("orderID")
                        String orderID) {


    }

}

