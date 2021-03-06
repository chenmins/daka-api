package org.chenmin.daka

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import weixin.popular.api.UserAPI
import weixin.popular.bean.user.User

@Transactional
class WxUserService {

    WxService wxService

    def dataSource

    boolean hasUser(String openid) {
        return get(openid)!=null
    }

    WxUser get(String openid){
        def u = WxUser.findByOpenid(openid)
//        println "~~~~WxUser~get~~~~~~"+openid
//        println u
        return u
    }

    int countByUnionid(String unionid){
        int spaid = 0
        def sql = new Sql(dataSource)
        String strSql = "select count(*) spaid from wx_user t where t.unionid='"+unionid+"'"
        sql.eachRow(strSql) {
            spaid = it.spaid
        }
        sql.close()
        return spaid
//        return WxUser.countByUnionid(unionid)
    }

    WxUser getByUnionid(String unionid){
        def u = WxUser.findByUnionid(unionid)
        println "~~~~WxUser~get~~getByUnionid~~~~"+unionid
        println u
        return u
    }

    void save(String openid,String popenid){
        String punionid = null
        if( popenid!=null && !popenid.isEmpty()){
            WxUser pu = get(popenid)
            punionid =  pu.unionid
        }
        String token =wxService.getTokenString("wx22617d41951fcc1f");
        User users = UserAPI.userInfo(token, openid,5);
        def u = new WxUser()
        u.openid = users.openid
        u.headimgurl = users.headimgurl
        u.nickname = users.nickname_emoji
        u.remark = users.remark
        u.subscribe = users.subscribe == 1
        u.subscribe_time = users.subscribe_time
        u.unionid = users.unionid
        u.popenid = popenid
        u.punionid = punionid
        u.save(flush: true)
    }

    void update(String openid,String popenid){
        String punionid = null
        if( popenid!=null && !popenid.isEmpty()){
            WxUser pu = get(popenid)
            punionid =  pu.unionid
        }
        WxUser u = get(openid)
        if(u==null)
            return
        if(u.popenid==null){
            u.popenid = popenid
            u.punionid = punionid
            u.save(flush: true)
        }
    }
}
