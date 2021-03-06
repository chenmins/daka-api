package org.chenmin.daka

import grails.converters.JSON
import groovy.sql.Sql

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
        //这个是真正的每日六点执行
        //cron name: 'todayTrigger', cronExpression: "0 0 6 * * ?"
    }
    def group = "todayGroup"
    def description = "每日早上6：00开始执行产生新的数据"

    def dataSource

    boolean jobs = (System.getProperty("jobs","false") == "true")

    def execute() {
        // execute job
        println DateTool.time()
        if(!jobs){
            println "skip TodayBoardJob!"
            return
        }
        def hasToday = TodayBoard.findByYmd(DateTool.today())
        if(hasToday!=null){
            println hasToday as JSON
            println DateTool.today()+" "+  DateTool.time() +" has exsit!"
            return
        }
        println DateTool.today()+" "+ DateTool.time()+" TodayBoard init!"
        int spaid = 0
        int currentCount = 0
        def sql = new Sql(dataSource)
        String strSql = "select sum(paid) spaid from daka_clock_user t "
        sql.eachRow(strSql) {
            spaid = it.spaid
        }
        strSql = "select count(paid) counts from daka_clock_user t where t.paid>0 "
        sql.eachRow(strSql) {
            currentCount = it.counts
        }
        //删除所有人的前一天的打卡日期
        strSql = "update daka_clock_user t set t.today_time = null,t.pour = true "
        int c = sql.executeUpdate(strSql)
        println c+" user today_time set null "
        //增加初始化的今日看板
        def earlyStar = ClockUser.get(1)
        def staminaStar = ClockUser.get(2)
        def today = new TodayBoard()
        today.ymd=DateTool.today()
        //当前挑战金
        today.currentTotalMoney=spaid
        //当前挑战人数
        today.currentParticipateCount=currentCount
        //当前已经打卡人数
        today.hitClock=0
        //当前已经未打卡人数
        today.notHitClock=today.currentParticipateCount
        today.earlyStar = earlyStar
        today.earlyTime = "06:30:00"
        today.staminaStar = staminaStar
        today.staminaCount = today.staminaStar.staminaCount
        today.hitMoney=0
        today.notHitMoney=today.currentTotalMoney
        today.thousandRewardMoney=-1
        today.startTime="06:30"
        today.endTime="07:30"
        today.needPay = false
        today.save(flush: true)
        sql.close()
        println DateTool.today()+" "+ DateTool.time()+" TodayBoard has created!"

    }
}
