package org.chenmin.daka

import grails.gorm.transactions.Transactional

@Transactional
class ClockUserService {

    boolean hasUser(String openid) {
        return get(openid)!=null
    }

    ClockUser get(String openid){
        def u =  ClockUser.findByOpenid(openid)
        println "~~~~ClockUser~get~~~~~~"+openid
        println u
        return u
    }

    ClockUser getByUnionid(String unionid){
        def u =  ClockUser.findByUnionid(unionid)
        println "~~~~ClockUser~findByUnionid~~~~~~"+unionid
        println u
        return u
    }
}
