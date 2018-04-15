package org.chenmin.daka

import grails.converters.JSON

class BootStrap {

    def init = { servletContext ->
        JSON.registerObjectMarshaller(Date) {

            return it?.format("yyyy-MM-dd HH:mm:ss")

        }
        if(ClockUser.get(1)!=null){
            println "It has init "
            return
        }
        //增加测试的早起之星
        def earlyStar = new ClockUser()
        earlyStar.openid = UUIDTool.getUUID()
        earlyStar.headImg = "https://avatar.csdn.net/9/A/C/3_liyintaoliuyun.jpg"
        earlyStar.nickname = "小红帽"
        earlyStar.unionid = UUIDTool.getUUID()
        earlyStar.todayTime = "06:30:02"
        earlyStar.staminaCount = 5
        earlyStar.paid = 3000
        earlyStar.cash = 123
        earlyStar.totalReward = 5123
        earlyStar.save()
        //增加测试的毅力之星
        def staminaStar = new ClockUser()
        staminaStar.openid = UUIDTool.getUUID()
        staminaStar.headImg = "https://avatar.csdn.net/8/7/B/3_kpchen_0508.jpg"
        staminaStar.nickname = "灰太狼"
        staminaStar.unionid = UUIDTool.getUUID()
        staminaStar.todayTime = "06:40:05"
        staminaStar.staminaCount = 25
        staminaStar.paid = 2300
        staminaStar.cash = 321
        staminaStar.totalReward = 2123
        staminaStar.save()
        //增加测试的今日看板
        earlyStar = ClockUser.get(1)
        staminaStar = ClockUser.get(2)
        def today = new TodayBoard()
        today.ymd=DateTool.today()
        today.currentTotalMoney=2360000
        today.currentParticipateCount=231
        today.hitClock=170
        today.notHitClock=61
        today.earlyStar = earlyStar
        today.earlyTime = today.earlyStar.todayTime
        today.staminaStar = staminaStar
        today.staminaCount = today.staminaStar.staminaCount
        today.hitMoney=2300000
        today.notHitMoney=60000
        today.thousandRewardMoney=531
        today.save()
        //增加打卡奖励
        def r1 = new RewardBoard()
        r1.user = earlyStar
        r1.openid = earlyStar.openid
        r1.ymd = DateTool.today()
        r1.ym = DateTool.month()
        r1.reward = 12
        r1.hitTime = DateTool.time()
        r1.hitType = "wx"
        r1.save()
        def r2 = new RewardBoard()
        r2.user = staminaStar
        r2.openid = staminaStar.openid
        r2.ymd = DateTool.today()
        r2.ym = DateTool.month()
        r2.reward = -1
        r2.hitTime = DateTool.time()
        r2.hitType = "wx"
        r2.save()
        //增加流水数据
        def cb1 = new CashBoard()
        cb1.user = earlyStar
        cb1.openid = earlyStar.openid
        cb1.cashType = "deposit"
        /**
         * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
         */
        cb1.cash = 1000
        cb1.remark = "付押金10元"
        cb1.save()

        println "It  init ok "

    }
    def destroy = {
    }
}
