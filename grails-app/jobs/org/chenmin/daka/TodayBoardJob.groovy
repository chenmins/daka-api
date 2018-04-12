package org.chenmin.daka

import grails.converters.JSON

class TodayBoardJob {
    def a = '''
这是参考格式
cronExpression: "s m h D M W Y"
                 | | | | | | `- Year [optional]
                 | | | | | `- Day of Week, 1-7 or SUN-SAT, ?
                 | | | | `- Month, 1-12 or JAN-DEC
                 | | | `- Day of Month, 1-31, ?
                 | | `- Hour, 0-23
                 | `- Minute, 0-59
                 `- Second, 0-59
'''
    static triggers = {
        cron name: 'todayTrigger', cronExpression: "0 * * * * ?"
    }
    def group = "todayGroup"
    def description = "每日早上6：00开始执行产生新的数据"

    def execute() {
        // execute job
        println DateTool.time()
        def hasToday = TodayBoard.findByYmd(DateTool.today())
        if(hasToday==null){
            println hasToday as JSON
            println DateTool.time()+" has exsit!"
            return
        }
        println DateTool.today()+" "+ DateTool.time()+" TodayBoard init!"
        //增加测试的今日看板
        def earlyStar = ClockUser.get(1)
        def staminaStar = ClockUser.get(2)
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
        println DateTool.today()+" "+ DateTool.time()+" TodayBoard has created!"

    }
}
