package org.chenmin.daka

import grails.gorm.transactions.Transactional
import weixin.popular.api.MessageAPI
import weixin.popular.bean.message.templatemessage.TemplateMessage
import weixin.popular.bean.message.templatemessage.TemplateMessageItem
import weixin.popular.bean.message.templatemessage.TemplateMessageMiniProgram
import weixin.popular.bean.message.templatemessage.TemplateMessageResult

import java.text.SimpleDateFormat

@Transactional
class WxMessageService {

    WxService wxService

//    SYaHOdJpBAh5A9axS111RNEJGm30buF1lDGpaem4Bsk
//    标题奖金发放通知
//    详细内容
//    {{first.DATA}}
//    合伙人：{{keyword1.DATA}}
//    结算等级：{{keyword2.DATA}}
//    发放金额：{{keyword3.DATA}}
//    发放时间：{{keyword4.DATA}}
//    {{remark.DATA}}
    def faMessage(String appid,String openid,
                    String firstString,String keyword1String,
                    String keyword2String, String keyword3String,String remarkString){
        TemplateMessage templateMessage  = new TemplateMessage()
        templateMessage.setTouser(openid)
        templateMessage.setTemplate_id("SYaHOdJpBAh5A9axS111RNEJGm30buF1lDGpaem4Bsk")
        LinkedHashMap<String, TemplateMessageItem> data = new LinkedHashMap<String, TemplateMessageItem>();
        TemplateMessageItem first=new TemplateMessageItem(firstString,"#0000FF");
        TemplateMessageItem keyword1=new TemplateMessageItem(keyword1String,"#FF0000");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TemplateMessageItem keyword2=new TemplateMessageItem(keyword2String,"#0000FF");
        TemplateMessageItem keyword3=new TemplateMessageItem(keyword3String,"#0000FF");
        TemplateMessageItem keyword4=new TemplateMessageItem(sdf.format(new Date()),"#0000FF");
        TemplateMessageItem remark = new TemplateMessageItem(remarkString,"#0000FF");
        data.put("first", first);
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        data.put("keyword3", keyword3);
        data.put("keyword4", keyword4);
        data.put("remark", remark);
        templateMessage.setData(data )
        templateMessage.setMiniprogram(wxa())
        TemplateMessageResult r = messageTemplateSend(appid,templateMessage)
        println "奖金发放通知：${firstString}"
        println "奖金发放通知openid：${openid}"
        println "msgid:"+r.msgid
        println "errmsg:"+r.errmsg
        println "errcode:"+r.errcode
        println r
    }

//    模版ID 8eqhZ_ox_l-7YwtNlbFecowfOzwtppK0E7gOOx9lt1A
//    推荐成功通知
//    详细内容
//    {{first.DATA}}
//    推荐人：{{keyword1.DATA}}
//    被推荐人：{{keyword2.DATA}}
//    {{remark.DATA}}
    def shareMessage(String appid,String openid,
                      String firstString,String keyword1String,
                      String keyword2String,String remarkString
    ) {
        TemplateMessage templateMessage  = new TemplateMessage()
        templateMessage.setTouser(openid)
        templateMessage.setTemplate_id("8eqhZ_ox_l-7YwtNlbFecowfOzwtppK0E7gOOx9lt1A")

        LinkedHashMap<String, TemplateMessageItem> data = new LinkedHashMap<String, TemplateMessageItem>();
        TemplateMessageItem first=new TemplateMessageItem(firstString,"#0000FF");
        TemplateMessageItem keyword1=new TemplateMessageItem(keyword1String,"#FF0000");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TemplateMessageItem keyword2=new TemplateMessageItem(keyword2String,"#0000FF");
        TemplateMessageItem remark=new TemplateMessageItem(remarkString+"\n"+sdf.format(new Date()),"#0000FF");
        data.put("first", first);
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        data.put("remark", remark);
        templateMessage.setData(data )
        templateMessage.setMiniprogram(wxa())
        messageTemplateSend(appid,templateMessage)
    }

    TemplateMessageMiniProgram wxa(){
        TemplateMessageMiniProgram mp = new TemplateMessageMiniProgram()
        mp.appid = "wxbd7ee929512fd71f"
        mp.pagepath = "pages/calc/calc"
        return mp
    }

//    gEhzNlrrfBYbyrrBSw-LFKvojQSJCw3sZTkXeq_jt-Y
//    标题 扫码成功通知
//    行业IT科技 - 互联网|电子商务
//    详细内容
//    {{first.DATA}}
//    扫码用户：{{keyword1.DATA}}
//    扫码类型：{{keyword2.DATA}}
//    扫码时间：{{keyword3.DATA}}
//    {{remark.DATA}}
    def scanMessage(String appid,String openid,
                    String firstString,String keyword1String,
                    String keyword2String,String remarkString){
        TemplateMessage templateMessage  = new TemplateMessage()
        templateMessage.setTouser(openid)
        templateMessage.setTemplate_id("gEhzNlrrfBYbyrrBSw-LFKvojQSJCw3sZTkXeq_jt-Y")

        LinkedHashMap<String, TemplateMessageItem> data = new LinkedHashMap<String, TemplateMessageItem>();
        TemplateMessageItem first=new TemplateMessageItem(firstString,"#0000FF");
        TemplateMessageItem keyword1=new TemplateMessageItem(keyword1String,"#FF0000");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TemplateMessageItem keyword2=new TemplateMessageItem(keyword2String,"#0000FF");
        TemplateMessageItem keyword3=new TemplateMessageItem(sdf.format(new Date()),"#0000FF");
        TemplateMessageItem remark = new TemplateMessageItem(remarkString,"#0000FF");
        data.put("first", first);
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        data.put("keyword3", keyword3);
        data.put("remark", remark);
        templateMessage.setData(data )
        templateMessage.setMiniprogram(wxa())
        messageTemplateSend(appid,templateMessage)
    }

    TemplateMessageResult messageTemplateSend(String appid, TemplateMessage templateMessage) {
       return MessageAPI.messageTemplateSend( wxService.getTokenString(appid),templateMessage)
    }
}