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

    def dataSource

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
        // 更新挑战人数
        int currentCount = 0
        strSql = "select count(paid) counts from daka_clock_user t where t.paid>0 "
        sql.eachRow(strSql) {
            currentCount = it.counts
        }
        def hasToday = TodayBoard.findByYmd(DateTool.today())
        hasToday.currentTotalMoney = spaid
        //当前挑战人数
        hasToday.currentParticipateCount=currentCount
        hasToday.save(flush: true)
        //返回用户对象
        def json =  earlyStar as JSON
        return json
    }

    //结算测试
    @GET
    @Path('/calc/{cash}/{paid}')
    @ApiOperation(value = "计算奖励金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String calc(
            @ApiParam(required = true, value = "预留金额（单位分）")
            @PathParam("cash")
                    int cash,
            @ApiParam(required = true, value = "补贴金额（单位分）")
            @PathParam("paid")
                    int paid) {
        //测试有没有结算过
        //检测打卡记录
        def has = CalcBoard.findByYmd(DateTool.today())
        if(has)
            return has as JSON
        //罚没未打卡的挑战金
        def cb = new CalcBoard()
        cb.ymd = DateTool.today()
        def sql = new Sql(dataSource)
        String strSql = "select sum(paid) all_paids from daka_clock_user u where u.paid>0"
        sql.eachRow(strSql) {
            cb.currentTotalMoney = it.all_paids
        }
        strSql = "select count(id) all_counts from daka_clock_user u where u.paid>0"
        sql.eachRow(strSql) {
            cb.currentParticipateCount = it.all_counts
        }
        strSql = "select count(id) clock_counts from daka_clock_user u where u.paid>0 and u.today_time is not null"
        sql.eachRow(strSql) {
            cb.hitClock = it.clock_counts
        }
        strSql = "select count(id) noclock_counts from daka_clock_user u where u.paid>0 and u.today_time is null"
        sql.eachRow(strSql) {
            cb.notHitClock = it.noclock_counts
        }
        strSql = "select sum(paid) clock_paids from daka_clock_user u where u.paid>0 and u.today_time is not null"
        sql.eachRow(strSql) {
            cb.hitMoney = it.clock_paids
        }
        strSql = "select sum(paid) noclock_paids from daka_clock_user u where u.paid>0 and u.today_time is null"
        sql.eachRow(strSql) {
            cb.notHitMoney = it.noclock_paids
        }

        cb.cash = cash
        cb.paid = paid
        //可瓜分金额，扣除预留
        cb.reals=cb.notHitMoney-cb.cash+cb.paid
        //算出费率
        double v = cb.reals/cb.hitMoney
        cb.thousandRewardMoney = Math.floor(v*1000*100)

        int fine = 0
        //罚没挑战金，记录流水，删除挑战金
        strSql = "select id from daka_clock_user u where u.paid>0 and u.today_time is null"
        sql.eachRow(strSql) {
            def cu = ClockUser.get(it.id)
            fine += cu.paid
            //增加流水数据
            def cb1 = new CashBoard()
            cb1.user = cu
            cb1.openid = cu.openid
            cb1.cashType = "fine"
            /**
             * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
             */
            cb1.cash = cu.paid
            cb1.remark = DateTool.today()+"未打卡，罚金${cu.paid/100}元"
            cb1.save(flush: true)
            cu.paid = 0
            cu.save(flush: true)
        }
        int reward = 0
        //发放奖励，记录流水，增加奖励金
        strSql = "select id from daka_clock_user u where u.paid>0 and u.today_time is not null"
        sql.eachRow(strSql) {
            def cu = ClockUser.get(it.id)
            //计算奖励
            int va = Math.floor(cu.paid * v)
            reward += va
            //增加流水数据
            def cb1 = new CashBoard()
            cb1.user = cu
            cb1.openid = cu.openid
            cb1.cashType = "reward"
            /**
             * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
             */
            cb1.cash = va
            cb1.remark = DateTool.today()+"坚持打卡，奖金${va/100}元"
            cb1.save(flush: true)
            cu.cash =  cu.cash + va
            cu.totalReward = cu.totalReward + va
            cu.save(flush: true)
            //奖励金日历变更
            def rb = RewardBoard.findByYmdAndOpenid(DateTool.today(),cu.openid)
            rb.reward = va
            rb.save(flush: true)
        }
        //平差价（四舍五入）
        cb.floors = fine - reward
        cb.remark ="今日发放摘要：" +
                "未打卡金额${cb.notHitMoney/100}," +
                "打卡金额${cb.hitMoney/100}," +
                "扣除${cb.cash/100}," +
                "补贴${cb.paid/100}," +
                "实际发放${cb.reals/100}," +
                "千份收益率${cb.thousandRewardMoney/100}," +
                "平差价:${cb.floors/100}"
        cb.save(flush: true)

        //更新早起之星和毅力之星
        def hasToday = TodayBoard.findByYmd(DateTool.today())
        int eid = 0
        strSql = "select id,today_time,stamina_count from daka_clock_user u where u.paid>0 and u.today_time is not null order by today_time asc limit 1"
        sql.eachRow(strSql) {
            eid = it.id
        }
        int sid = 0
        strSql = "select id,today_time,stamina_count from daka_clock_user u where u.paid>0 and u.today_time is not null order by stamina_count desc limit 1"
        sql.eachRow(strSql) {
            sid = it.id
        }
        def earlyStar = ClockUser.get(eid)
        def staminaStar = ClockUser.get(sid)
        hasToday.earlyStar = earlyStar
        hasToday.earlyTime = earlyStar.todayTime
        hasToday.staminaStar = staminaStar
        hasToday.staminaCount = staminaStar.staminaCount
        //更新每日表的发放状态和调整后的打卡数据
        hasToday.currentParticipateCount=cb.reals
        hasToday.calc = true
        hasToday.save(flush: true)
        return cb as JSON
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
            r1.paid = earlyStar.paid
            r1.reward = -1
            r1.hitTime = DateTool.time()
            r1.hitType = "wx"
            r1.save(flush: true)
            //打卡成功，修改今日记录
            earlyStar.todayTime = DateTool.time()
            earlyStar.staminaCount = earlyStar.staminaCount+1
            earlyStar.save(flush: true)
        }
        //此处可能会出现并发问题
        int spaid = 0
        def sql = new Sql(dataSource)
        String strSql = "select sum(paid) spaid from daka_reward_board t where t.ymd='"+DateTool.today()+"'"
        sql.eachRow(strSql) {
            spaid = it.spaid
        }
        int counts = 0
        strSql = "select count(paid) counts from daka_reward_board t where t.ymd='"+DateTool.today()+"'"
        sql.eachRow(strSql) {
            counts = it.counts
        }
        //更新每日显示的打卡人数，和打卡金额
        def today = TodayBoard.findByYmd(DateTool.today())
        today.hitMoney = spaid
        today.notHitMoney =  today.currentTotalMoney - spaid
        today.hitClock = counts
        today.notHitClock = today.currentParticipateCount - counts
        today.save(flush: true)
        return earlyStar as JSON
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

