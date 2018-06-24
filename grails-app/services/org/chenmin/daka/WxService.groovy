package org.chenmin.daka

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.apache.http.client.HttpClient
import weixin.popular.api.MediaAPI
import weixin.popular.api.QrcodeAPI
import weixin.popular.bean.media.Media
import weixin.popular.bean.media.MediaType
import weixin.popular.bean.qrcode.QrcodeTicket

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Transactional
class WxService  {

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

    String getTokenString(String appid){
        String token = getToken(  appid)
        def t = JSON.parse(token)
        return t.msg
    }

    void createQrcode(String appid,String code,String openid){
        String token = getTokenString(appid)
        QrcodeTicket qr =  QrcodeAPI.qrcodeCreateFinal(token,code)
        BufferedImage bqr = QrcodeAPI.showqrcode(qr.ticket)
        final File htmlFile = File.createTempFile("temp"+System.currentTimeMillis(), ".jpg");//创建临时文件
        println "path:"+htmlFile.getAbsolutePath()
        ImageIO.write(bqr, "jpg", htmlFile);
        // 上传微信服务器
        Media media = MediaAPI.mediaUpload(token, MediaType.image, htmlFile);
        String media_id = media.getMedia_id();
        CustomserviceAPI.custom_send_Image(token, openid, media_id);
        htmlFile.deleteOnExit()
    }

}
