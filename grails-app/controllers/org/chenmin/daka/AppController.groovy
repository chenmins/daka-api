package org.chenmin.daka

class AppController {

    ClockUserService clockUserService

    WxUserService wxUserService

    def index() {
        if(params.unionid!=null){
            ClockUser du = clockUserService.getByUnionid(params.unionid)
            flash.du =  du
            WxUser wu = wxUserService.getByUnionid(params.unionid)
            flash.wu =  wu
        }
        render(view: 'index')
    }
}
