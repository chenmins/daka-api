package org.chenmin.daka

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import weixin.popular.api.BaseAPI
import weixin.popular.api.MediaAPI
import weixin.popular.api.QrcodeAPI
import weixin.popular.bean.BaseResult
import weixin.popular.bean.media.Media
import weixin.popular.bean.media.MediaType
import weixin.popular.bean.message.message.ImageMessage
import weixin.popular.bean.message.message.Message
import weixin.popular.bean.message.message.TextMessage
import weixin.popular.bean.qrcode.QrcodeTicket
import weixin.popular.client.LocalHttpClient
import weixin.popular.util.JsonUtil

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.charset.Charset

@Transactional
class WxService extends BaseAPI{

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
        Media media = MediaAPI.mediaUpload(token, MediaType.image, new File(htmlFile));
        String media_id = media.getMedia_id();
        custom_send_Image(token, openid, media_id);
        htmlFile.deleteOnExit()
    }

    public static BaseResult custom_send(String access_token, String json) {
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/message/custom/send")
                .addParameter(getATPN(), access_token)
                .setEntity(new StringEntity(json, Charset.forName("UTF-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest,
                BaseResult.class);
    }

    public static BaseResult custom_send(String access_token, Message json) {
        return custom_send(access_token, JsonUtil.toJSONString(json));
    }

    public static BaseResult custom_send_Text(String access_token,
                                              String touser, String content) {
        TextMessage json = new TextMessage(touser, content);
        return custom_send(access_token, json);
    }

    public static BaseResult custom_send_Image(String access_token,
                                               String touser, String mediaId) {
        ImageMessage json = new ImageMessage(touser, mediaId);
        return custom_send(access_token, json);
    }
}
