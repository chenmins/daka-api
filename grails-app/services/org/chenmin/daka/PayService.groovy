package org.chenmin.daka

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import weixin.popular.bean.paymch.MchPayNotify

@Transactional
class PayService {

    def dataSource
    //从官方获取
    public static final String APPID = "wx22617d41951fcc1f"
    //oJRLp04tlU0f5HHPwMcz5YWo4kVk 盼盼
    public static final String ADMIN_OPENID = "oJRLp04tlU0f5HHPwMcz5YWo4kVk"
    ClockUserService clockUserService
    WxMessageService wxMessageService
    WxUserService wxUserService

    def payfor(String openid, MchPayNotify payNotify) {
        def wpt = new WxPayTicket()
        wpt.appid = payNotify.appid
        wpt.attach = payNotify.attach
        wpt.bank_type = payNotify.bank_type
        wpt.cash_fee = payNotify.cash_fee
        wpt.fee_type = payNotify.fee_type
        wpt.is_subscribe = payNotify.is_subscribe
        wpt.mch_id = payNotify.mch_id
        wpt.openid = openid
        wpt.nonce_str = payNotify.nonce_str
        wpt.out_trade_no = payNotify.out_trade_no
        wpt.result_code = payNotify.result_code
        wpt.return_code = payNotify.return_code
        wpt.sign = payNotify.sign
        wpt.time_end = payNotify.time_end
        wpt.total_fee = payNotify.total_fee
        wpt.trade_type = payNotify.trade_type
        wpt.transaction_id = payNotify.transaction_id
        wpt.save(flush: true)

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
        cb1.cash =  wpt.total_fee
        cb1.remark = "付押金"+(wpt.total_fee/100)+"元"
        cb1.refund = "-1"
        cb1.orderID= payNotify.out_trade_no
        cb1.save(flush: true)
        //修改用户押金
        earlyStar.paid = earlyStar.paid + wpt.total_fee
        earlyStar.save(flush: true)
        int spaid = 0
        def sql = new Sql(dataSource);
        String strSql = "select ifnull(sum(paid),0) spaid from daka_clock_user t ";
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
        println "#~~~~payMchNotify5~~~~~SUCCESS chenmin~~~~~~~~~~~~~~"
        //核算首冲奖励
        //select count(*) c from daka_cash_board a where a.cash_type ='deposit' and a.openid='oIvCJ5RwdgZQFiM04vIPa0Rq9LvQ';
        int payCount = 0
        strSql = "select count(*) c from daka_cash_board a where a.cash_type ='deposit' and a.openid='"+openid+"'"
        println strSql
        sql.eachRow(strSql) {
            payCount = it.c
        }
        if(payCount==1){
            //属于第一次充值
            println "#~~~~payMchNotify6~~~~~first~~~~~~~~~~~~~~"
            //查询出推荐人ID
            def u = ClockUser.findByOpenid(openid)
            String popenid =  u.popenid
            if(popenid){
                //有推荐人
                println "#~~~~payMchNotify7~~~~~has popenid~~~~~~~~~~~~~~"
                FirstReward fr = new FirstReward()
                fr.openid = openid
                fr.popenid = popenid
                fr.cash = 100
                fr.pcash = 100
                fr.remark = '20180527政策，首冲各奖励1元'
                fr.save(flush: true)
                //记录首冲人奖励
                //增加流水数据
                def cbu = new CashBoard()
                cbu.user = u
                cbu.openid = openid
                cbu.cashType = "first"
                /**
                 * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款，first：首冲奖励）
                 */
                cbu.cash =100
                cbu.remark = "首冲奖励${cbu.cash/100}元"
                cbu.save(flush: true)
                //记录推荐人奖励
                def pu =  ClockUser.findByOpenid(popenid)
                def cbp = new CashBoard()
                cbp.user = pu
                cbp.openid = popenid
                cbp.cashType = "first"
                /**
                 * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款，first：首冲奖励）
                 */
                cbp.cash =100
                cbp.remark = "推荐${u.nickname}首冲奖励${cbp.cash/100}元"
                cbp.save(flush: true)
                //变更首冲人余额和累计奖励
                u.cash = u.cash+cbu.cash
                u.totalReward = u.totalReward +cbu.cash
                //变更推荐人余额和累计奖励
                pu.cash = pu.cash+cbp.cash
                pu.totalReward = pu.totalReward +cbp.cash
                u.save(flush: true)
                pu.save(flush: true)
                println "#~~~~payMchNotify8~~~~~FirstReward ok~~~~~~~~~~~~~~"
                //反向查找公众号的id
                def wxu = wxUserService.getByUnionid(earlyStar.unionid)
                def wxopenid = wxu.openid

                def pwxu = wxUserService.getByUnionid(pu.unionid)
                def pwxopenid = pwxu.openid

                //发放奖励通知，充值人，和推荐人各自一个
                wxMessageService.faMessage(APPID,wxopenid,"首冲奖励",pu.nickname,"青铜","${cbp.cash / 100}元",cbu.remark)
                wxMessageService.faMessage(APPID,pwxopenid,"首冲奖励",pu.nickname,"青铜","${cbp.cash / 100}元",cbp.remark)
                wxMessageService.faMessage(APPID,ADMIN_OPENID,"首冲奖励",pu.nickname,"青铜","${cbp.cash / 100}元",cbp.remark)
            }
        }
    }
}
