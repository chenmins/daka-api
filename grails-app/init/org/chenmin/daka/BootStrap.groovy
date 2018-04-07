package org.chenmin.daka

class BootStrap {

    def init = { servletContext ->
        if(ClockUser.get(1)!=null){
            println "It has init "
            return
        }

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

        println "It  init ok "

    }
    def destroy = {
    }
}
