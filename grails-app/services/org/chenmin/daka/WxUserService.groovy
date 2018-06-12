package org.chenmin.daka

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import weixin.popular.api.UserAPI
import weixin.popular.bean.user.User

@Transactional
class WxUserService {

    WxService wxService

    boolean hasUser(String openid) {
        return get(openid)!=null
    }

    WxUser get(String openid){
        def u = WxUser.findByOpenid(openid)
        println "~~~~WxUser~get~~~~~~"+openid
        println u
        return u
    }

    void save(String openid,String popenid){
        String token =wxService.getToken("wx22617d41951fcc1f");
        println "token:"+token
        def t = JSON.parse(token)
        println "t.msg:"+t.msg
        User users = UserAPI.userInfo(t.msg, openid,5);
        def u = new WxUser()
        u.openid = users.openid
        u.headimgurl = users.headimgurl
        u.nickname = users.nickname_emoji
        u.remark = users.remark
        u.subscribe = users.subscribe == 1
        u.subscribe_time = users.subscribe_time
        u.unionid = users.unionid
        u.popenid = popenid
        u.save(flush: true)
    }
}
