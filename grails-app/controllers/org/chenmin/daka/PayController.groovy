package org.chenmin.daka

import grails.converters.JSON
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.Unifiedorder
import weixin.popular.bean.paymch.UnifiedorderResult
import weixin.popular.util.PayUtil

class PayController {

    def index() {
        String url = "http://wx.bdh114.com/default/PayMchNotify"
        String appid = "wxbd7ee929512fd71f"
        String mch_id = "1490841962"
        String key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2"
        String wx_openid = params.id
        String fee = "1"
        Unifiedorder unifiedorder = new Unifiedorder();
        unifiedorder.setAppid(appid);
        unifiedorder.setMch_id(mch_id);
        unifiedorder.setNonce_str(UUID.randomUUID().toString().toString().replace("-", ""));

        unifiedorder.setOpenid(wx_openid);
        unifiedorder.setBody("商品信息");
        unifiedorder.setOut_trade_no("123456");
        unifiedorder.setTotal_fee(fee);//单位分
        unifiedorder.setSpbill_create_ip(request.getRemoteAddr());//IP
        unifiedorder.setNotify_url(url);
        unifiedorder.setTrade_type("JSAPI");//JSAPI，NATIVE，APP，WAP
        //统一下单，生成预支付订单
        UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,key);
        println "unifiedorderResult:"+params.id
        println unifiedorder as JSON
        println unifiedorderResult as JSON


        //@since 2.8.5  API返回数据签名验证
        if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status()){
            String json = PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(), appid, key);
            println "unifiedorderResult js"
            println json
//            //将json 传到jsp 页面
//            request.setAttribute("json", json);
//            //示例jsp
//            request.getRequestDispatcher("pay_example.jsp").forward(request,response);
            render json
            return
        }
        render unifiedorderResult as JSON
        return
    }
}
