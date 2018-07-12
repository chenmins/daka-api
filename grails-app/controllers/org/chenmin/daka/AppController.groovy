package org.chenmin.daka

class AppController {

    ClockUserService clockUserService

    WxUserService wxUserService

    def index() {
        println "params.unionid :"+params.unionid
        if(params.unionid!=null){
            flash.du = clockUserService.countByUnionid(params.unionid)
            println "flash.du :"+flash.du
            flash.wu =  wxUserService.countByUnionid(params.unionid)
            println "flash.wu :"+flash.wu
        }
        render(view: 'index')
    }

}
