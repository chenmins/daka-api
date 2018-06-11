package org.chenmin.daka

import grails.gorm.transactions.Transactional
import org.apache.http.client.HttpClient

@Transactional
class WxService {

    String init(String appid,String secret){
        def url = "http://wx.bdh114.com:8088/weixinkey/resteasy/token/init"
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient()
        def p = new HashMap<String,String>()
        p.appid = appid
        p.secret = secret
        def s = new HashMap<String,String>()
        def json = HttpClientTools.postForm(hc,url,p,s)
        chc.close()
        return json
    }


    String getToken(String appid){
        def url = "http://wx.bdh114.com:8088/weixinkey/resteasy/token/getToken"
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient()
        def p = new HashMap<String,String>()
        p.appid = appid
        def s = new HashMap<String,String>()
        def json = HttpClientTools.postForm(hc,url,p,s)
        chc.close()
        return json
    }

}
