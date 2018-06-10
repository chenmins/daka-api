package org.chenmin.daka

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.sql.Sql

@Transactional
class CalcService {

    def dataSource

    /**
     * 结算测试
     * @param cash 手续费
     * @param paid 补贴
     * @return
     */
    String calc(int cash,int paid) {
        //测试有没有结算过
        //检测打卡记录
        def has = CalcBoard.findByYmd(DateTool.today())
        if(has)
            return has as JSON
        println "开始结算手续费扣除${cash/100}元,补贴${paid/100}元"
        //罚没未打卡的挑战金
        def cb = new CalcBoard()
        cb.ymd = DateTool.today()
        def sql = new Sql(dataSource)
        String strSql = ""
        //计算截止昨晚凌晨交押金数和未打卡人数
        strSql = "select count(id) c,ifnull(sum(sc),0) sc from (" +
                "    select cu.id,cu.nickname,cu.paid,cb.sc,cu.today_time from (" +
                "    select * from daka_clock_user u where u.paid>0 and u.today_time is null and u.pour = true" +
                "    ) cu,(" +
                "    select openid,ifnull(sum(cash),0)  sc from daka_cash_board where refund =-1 and date_created < curdate() group by openid" +
                "    )cb where cu.openid = cb.openid" +
                ") a"
        sql.eachRow(strSql) {
            cb.notHitClock = it.c
            cb.notHitMoney = it.sc
        }
        //计算截止昨晚凌晨交押金数和已经打卡人数
        strSql = "select count(id) c,ifnull(sum(sc),0) sc from (" +
                "    select cu.id,cu.nickname,cu.paid,cb.sc,cu.today_time from (" +
                "    select * from daka_clock_user u where u.paid>0 and u.today_time is not null and u.pour = true" +
                "    ) cu,(" +
                "    select openid,ifnull(sum(cash),0)  sc from daka_cash_board where refund =-1 and date_created < curdate() group by openid" +
                "    )cb where cu.openid = cb.openid" +
                ") a"
        sql.eachRow(strSql) {
            cb.hitClock = it.c
            cb.hitMoney = it.sc
        }
        cb.currentTotalMoney = cb.notHitMoney+cb.hitMoney
        cb.currentParticipateCount = cb.notHitClock+ cb.hitClock
        cb.cash = cash
        cb.paid = paid
        //可瓜分金额，扣除预留
        cb.reals=cb.notHitMoney-cb.cash
        //算出费率
        double v = cb.reals/cb.hitMoney
        cb.thousandRewardMoney = Math.floor(v*1000*100)
        int fine = 0
        //罚没挑战金，记录流水，删除挑战金
        //strSql = "select id from daka_clock_user u where u.paid>0 and u.today_time is null and u.pour = true"
        strSql ="select cu.id,cu.nickname,cu.paid,cb.sc,cu.today_time from (" +
                "select * from daka_clock_user u where u.paid>0 and u.today_time is null and u.pour = true" +
                ") cu,(" +
                "select openid,ifnull(sum(cash),0) sc from daka_cash_board where refund =-1 and date_created < curdate() group by openid" +
                ")cb where cu.openid = cb.openid"
        sql.eachRow(strSql) {
            def cu = ClockUser.get(it.id)
            fine += it.sc
            //增加流水数据
            def cb1 = new CashBoard()
            cb1.user = cu
            cb1.openid = cu.openid
            cb1.cashType = "fine"
            /**
             * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
             */
            //TODO 不能把今天的加进去
            cb1.cash = it.sc*-1
            cb1.remark = DateTool.today()+"未打卡，罚金${it.sc/100}元"
            cb1.save(flush: true)

            //昨天以前的押金罚没，修改现金日志表
            //update daka_cash_board set refund = 2 where date_created < curdate() and openid = 'oIvCJ5fUZdEh9YWfiE2I7c1m9E6o';
            strSql = "update daka_cash_board set refund = 2 where date_created < curdate() and refund = -1 and openid ='"+cu.openid+"'"
            int c = sql.executeUpdate(strSql)
            println c+" is refund =2 ,openid: "+cu.openid

            //改完现金日志表才可以罚钱，并且今天的不罚钱
            //select  sum(cash) sc from daka_cash_board where refund =-1 and openid = 'oIvCJ5XXUSGAl7_FvMRdMtFjtTv8'
            strSql = "select sum(cash) sc from daka_cash_board where refund =-1 and openid = '"+cu.openid+"'"
            println strSql

            sql.eachRow(strSql) {
                if(it.sc==null){
                    println "sum(cash)  is null "+cu.openid
                    cu.paid = 0
                }else{
                    println "sum(cash)  is "+ it.sc+" ,"+cu.openid
                    cu.paid = it.sc
                }

            }
            cu.staminaCount = 0 //删除持续值
            cu.pour = false//改为没下注
            cu.save(flush: true)
        }
        int reward = 0
        //人均补贴
        int pp = 0
        if(paid>0){
            pp = Math.floor(paid/cb.hitClock)
        }
        //发放奖励，记录流水，增加奖励金
//        strSql = "select id from daka_clock_user u where u.paid>0 and u.today_time is not null and u.pour = true"
        strSql ="select cu.id,cu.nickname,cu.paid,cb.sc,cu.today_time from (" +
                "select * from daka_clock_user u where u.paid>0 and u.today_time is not null and u.pour = true" +
                ") cu,(" +
                "select openid,ifnull(sum(cash),0) sc from daka_cash_board where refund =-1 and date_created < curdate() group by openid" +
                ")cb where cu.openid = cb.openid"
        sql.eachRow(strSql) {
            def cu = ClockUser.get(it.id)
            def rb = RewardBoard.findByYmdAndOpenid(DateTool.today(),cu.openid)
            //计算奖励
            int va = Math.floor(it.sc * v)
            reward += va
            //增加瓜分流水数据
            def cb1 = new CashBoard()
            cb1.user = cu
            cb1.openid = cu.openid
            cb1.cashType = "reward"
            /**
             * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
             */
            cb1.cash = va
            cb1.remark = DateTool.today()+"坚持打卡，挑战金${it.sc/100}元,奖金${va/100}元"
            cb1.save(flush: true)
            cu.cash =  cu.cash + va
            cu.totalReward = cu.totalReward + va
            //cu.save(flush: true)
            if(pp>0){
                //增加补贴流水数据
                def cb2 = new CashBoard()
                cb2.user = cu
                cb2.openid = cu.openid
                cb2.cashType = "paid"
                /**
                 * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款，paid：补贴）
                 */
                cb2.cash = pp
//                cb2.remark = DateTool.today()+"坚持打卡，补贴${paid/100}元,分到${pp/100}元"
                cb2.remark = DateTool.today()+"坚持打卡，平台补贴${pp/100}元"

                cb2.save(flush: true)
                reward += pp

                cu.cash =  cu.cash + pp
                cu.totalReward = cu.totalReward + pp
            }

            cu.save(flush: true)
            //奖励金日历变更
            rb.reward = va + pp
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
        hasToday.notHitMoney=cb.reals
        //扣掉的钱加上去cb.hitMoney
        hasToday.hitMoney = cb.hitMoney + cash
        hasToday.calc = true
        //更新罚款后的实时挑战金
        strSql = "select ifnull(sum(paid),0) clock_paids from daka_clock_user u where u.paid>0"
        sql.eachRow(strSql) {
            hasToday.currentTotalMoney = it.clock_paids
        }
        //更新罚款后的实时挑战人数
        strSql = "select count(paid) counts from daka_clock_user u where u.paid>0"
        sql.eachRow(strSql) {
            hasToday.currentParticipateCount = it.counts
        }
        hasToday.save(flush: true)
        sql.close()
        return cb as JSON
    }
}
