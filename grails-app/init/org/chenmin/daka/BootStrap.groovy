package org.chenmin.daka

class BootStrap {

    def init = { servletContext ->
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
        earlyStar.cash = 5123
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
        staminaStar.cash = 2123
        staminaStar.save()
        //增加测试的今日看板
        def today = new TodayBoard()
        today.ymd=DateTool.today()
        today.currentTotaloney=2360000
        today.currentParticipateCount=231
        today.todayHitClock=170
        today.todayNotHitClock=61
        today.earlyStar = ClockUser.get(1)
        today.earlyTime = today.earlyStar.todayTime
        today.staminaStar = ClockUser.get(2)
        today.staminaCount = today.staminaStar.staminaCount
        today.save()

        println "It  init ok "

    }
    def destroy = {
    }
}
