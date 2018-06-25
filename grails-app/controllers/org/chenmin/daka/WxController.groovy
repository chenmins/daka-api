package org.chenmin.daka

import grails.converters.JSON
import weixin.popular.api.UserAPI
import weixin.popular.bean.message.EventMessage
import weixin.popular.bean.user.User
import weixin.popular.bean.xmlmessage.XMLMessage
import weixin.popular.bean.xmlmessage.XMLTextMessage
import weixin.popular.support.ExpireKey
import weixin.popular.support.expirekey.DefaultExpireKey
import weixin.popular.util.SignatureUtil
import weixin.popular.util.XMLConverUtil

import javax.servlet.ServletInputStream
import javax.servlet.ServletOutputStream

class WxController {
    //从官方获取
    public static final String APPID = "wx22617d41951fcc1f"
    //oJRLp04tlU0f5HHPwMcz5YWo4kVk 盼盼
    public static final String ADMIN_OPENID = "oJRLp04tlU0f5HHPwMcz5YWo4kVk"

    private String token = "test";
    WxService wxService
    WxMessageService wxMessageService
    WxUserService wxUserService

    //重复通知过滤
    private static ExpireKey expireKey = new DefaultExpireKey();
    def index() {
        render "Hello WX"
    }
    def api() {
        ServletInputStream inputStream = request.getInputStream();
        ServletOutputStream outputStream = response.getOutputStream();
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        //首次请求申请验证,返回echostr
        if(echostr!=null){
            outputStreamWrite(outputStream,echostr);
            response.flushBuffer()
            return
        }

        //验证请求签名
        if(!signature.equals(SignatureUtil.generateEventMessageSignature(token,timestamp,nonce))){
            System.out.println("The request signature is invalid");
            response.flushBuffer()
            return;
        }

        if(inputStream!=null){
            //转换XML
            EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class,inputStream);
            final String openid =  eventMessage.getFromUserName();
            println eventMessage
            println XMLConverUtil.convertToXML(eventMessage)
            String key =  eventMessage.getMsgId()
            if(expireKey.exists(key)){
                //重复通知不作处理
                response.flushBuffer()
                return;
            }else{
                expireKey.add(key);
            }

            if (eventMessage.getMsgType().equals("event")) {
                if (eventMessage.getEvent().equals("CLICK")) {
                    if(eventMessage.getEventKey().equals("my_qrcode")){
                        Runnable r = new Runnable() {
                            public void run() {
                                wxService.createQrcode(APPID,openid,openid)
                            }
                        };
                        Thread t = new Thread(r);
                        t.start();
                        //创建回复
                        XMLMessage xmlTextMessage = new XMLTextMessage(
                                eventMessage.getFromUserName(),
                                eventMessage.getToUserName(),
                                "二维码正在制作中，稍后会自动发送");
                        //回复
                        xmlTextMessage.outputStreamWrite(outputStream);
                        response.flushBuffer()
                        return;
                    }
                }
                if (eventMessage.getEvent().equals("subscribe")) {
                    String eventKey = eventMessage.getEventKey()
                    String qrscene_ = "qrscene_"
                    if(!eventKey.isEmpty()&&eventKey.startsWith(qrscene_)){
                        eventKey = eventKey.substring(qrscene_.length())
                        if(!wxUserService.hasUser(openid)){
                            println "${openid}不存在,关系eventKey：${eventKey}"
                            wxUserService.save(openid,eventKey)
                        }else{
                            println "${openid}存在,更新eventKey：${eventKey}"
                            wxUserService.update(openid,eventKey)
                        }

                        WxUser pu = wxUserService.get(eventKey)
                        def newUser  = wxUserService.get(openid)
                        //通知贡献者
                        wxMessageService.scanMessage(APPID,eventKey,"感谢你的推广",""+newUser.nickname,"扫描推广二维码","")
                        //推荐成功通知ADMIN_OPENID
                        wxMessageService.shareMessage(APPID,ADMIN_OPENID,"推广提示",pu.nickname,""+newUser.nickname,"扫描推广二维码")
                        //感谢扫描者
                        String msg = "感谢扫描，由【"+pu.nickname+"】分享的二维码"
                        //创建回复
                        XMLMessage xmlTextMessage = new XMLTextMessage(
                                eventMessage.getFromUserName(),
                                eventMessage.getToUserName(),
                                msg);
                        //回复
                        xmlTextMessage.outputStreamWrite(outputStream);
                        response.flushBuffer()
                        return;
                    }
                }
                if (eventMessage.getEvent().equals("SCAN")) {
                    String eventKey = eventMessage.getEventKey()
                    if(!wxUserService.hasUser(openid)){
                        println "${openid}不存在,关系eventKey：${eventKey}"
                        wxUserService.save(openid,eventKey)
                    }else{
                        println "${openid}存在,更新eventKey：${eventKey}"
                        wxUserService.update(openid,eventKey)
                    }
                    WxUser pu = wxUserService.get(eventKey)
                    def newUser  = wxUserService.get(openid)
                    //通知贡献者
                    wxMessageService.scanMessage(APPID,eventKey,"感谢你的推广",""+newUser.nickname,"扫描推广二维码","")
                    //推荐成功通知ADMIN_OPENID
                    wxMessageService.shareMessage(APPID,ADMIN_OPENID,"推广提示",pu.nickname,""+newUser.nickname,"扫描推广二维码")
                    //感谢扫描者
                    String msg = "感谢扫描，由【"+pu.nickname+"】分享的二维码"
                    //创建回复
                    XMLMessage xmlTextMessage = new XMLTextMessage(
                            eventMessage.getFromUserName(),
                            eventMessage.getToUserName(),
                            msg);
                    //回复
                    xmlTextMessage.outputStreamWrite(outputStream);
                    response.flushBuffer()
                    return;
                }
            }
            //抓取用戶信息入庫
            //TODO 帶參數的二維碼，要記錄popenid
            if(!wxUserService.hasUser(openid)){
                println "${openid}不存在"
                wxUserService.save(openid,null)
            }
            def user  = wxUserService.get(openid)
            //创建回复
            XMLMessage xmlTextMessage = new XMLTextMessage(
                    eventMessage.getFromUserName(),
                    eventMessage.getToUserName(),
                    "你好，"+user.nickname+",系統正在开发中，请使用小程序测试功能");
            //回复
            xmlTextMessage.outputStreamWrite(outputStream);
            String token =wxService.getTokenString(APPID);
            User users = UserAPI.userInfo(token, openid,5);
            println users as JSON
            response.flushBuffer()
            return;
        }
        outputStreamWrite(outputStream,"");
        response.flushBuffer()
        return;
    }

    /**
     * 数据流输出
     * @param outputStream
     * @param text
     * @return
     */
    private boolean outputStreamWrite(OutputStream outputStream,String text){
        try {
            outputStream.write(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
