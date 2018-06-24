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
import java.awt.Graphics
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
        File bg =  new File("/home/bae/app/bg.jpg")
        BufferedImage ibg = ImageIO.read(bg);
        String token = getTokenString(appid)
        QrcodeTicket qr =  QrcodeAPI.qrcodeCreateFinal(token,code)
        BufferedImage bqr = QrcodeAPI.showqrcode(qr.ticket)
        BufferedImage combined = new BufferedImage(ibg.getWidth(), ibg.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = combined.getGraphics();
        g.drawImage(ibg, 0, 0, null);
        //左右拼接
        //g.drawImage(image2, image1.getWidth(), 0, null);
        //上下拼接
        g.drawImage(bqr, 350, 1050, null);


        final File htmlFile = File.createTempFile("temp"+System.currentTimeMillis(), ".jpg");//创建临时文件
        println "path:"+htmlFile.getAbsolutePath()
        ImageIO.write(combined, "jpg", htmlFile);
        // 上传微信服务器
        Media media = MediaAPI.mediaUpload(token, MediaType.image, htmlFile);
        String media_id = media.getMedia_id();
        CustomserviceAPI.custom_send_Image(token, openid, media_id);
        htmlFile.deleteOnExit()
    }

}
