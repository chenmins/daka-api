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
    private String token = "test";
    WxService wxService
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
            String key = eventMessage.getFromUserName() + "__"
            + eventMessage.getToUserName() + "__"
            + eventMessage.getMsgId() + "__"
            + eventMessage.getCreateTime();
            if(expireKey.exists(key)){
                //重复通知不作处理
                response.flushBuffer()
                return;
            }else{
                expireKey.add(key);
            }

            //创建回复
            XMLMessage xmlTextMessage = new XMLTextMessage(
                    eventMessage.getFromUserName(),
                    eventMessage.getToUserName(),
                    "你好");
            //回复
            xmlTextMessage.outputStreamWrite(outputStream);
            String openid =  eventMessage.getFromUserName();
            String token =wxService.getToken("wx22617d41951fcc1f");

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
