package org.chenmin.daka

import grails.gorm.transactions.Transactional
import groovy.sql.Sql

@Transactional
class ClockUserService {

    def dataSource

    boolean hasUser(String openid) {
        return get(openid)!=null
    }

    ClockUser get(String openid){
        def u =  ClockUser.findByOpenid(openid)
        println "~~~~ClockUser~get~~~~~~"+openid
        println u
        return u
    }

    int countByUnionid(String unionid){
        int spaid = 0
        def sql = new Sql(dataSource)
        String strSql = "select count(*) spaid from daka_clock_user t where t.unionid='"+unionid+"'"
        sql.eachRow(strSql) {
            spaid = it.spaid
        }
        sql.close()
        return spaid
//        return ClockUser.countByUnionid(unionid)
    }

    ClockUser getByUnionid(String unionid){
        def u =  ClockUser.findByUnionid(unionid)
        println "~~~~ClockUser~findByUnionid~~~~~~"+unionid
        println u
        return u
    }
}
