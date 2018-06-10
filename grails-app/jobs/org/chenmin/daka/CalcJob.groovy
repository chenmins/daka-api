package org.chenmin.daka

class CalcJob {
    static triggers = {
//        cron name: 'todayCalc', cronExpression: "0 * * * * ?"
        //这个是真正的每日17:30执行
        cron name: 'calcTrigger', cronExpression: "0 30 17 * * ?"
    }

    CalcService calcService

    boolean jobs = (System.getProperty("jobs","false") == "true")

    def execute() {
        // execute job
        println DateTool.time()
        if(!jobs){
            println "skip CalcJob!"
            return
        }
        String calc = calcService.calc(0,0)
        println calc
    }
}
