package org.chenmin.daka

import grails.converters.JSON
import org.springframework.web.bind.annotation.ResponseBody
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.MchBaseResult
import weixin.popular.bean.paymch.MchPayNotify
import weixin.popular.bean.paymch.Unifiedorder
import weixin.popular.bean.paymch.UnifiedorderResult
import weixin.popular.support.ExpireKey
import weixin.popular.support.expirekey.DefaultExpireKey
import weixin.popular.util.PayUtil
import weixin.popular.util.SignatureUtil
import weixin.popular.util.StreamUtils
import weixin.popular.util.XMLConverUtil

import java.nio.charset.Charset

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
        unifiedorder.setOut_trade_no(UUID.randomUUID().toString().toString().replace("-", ""));
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

    //重复通知过滤
    private static ExpireKey expireKey = new DefaultExpireKey();
    private String key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2";	//mch key

    @ResponseBody
    def payMchNotify() {
        //获取请求数据
        String xmlData = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
        println "#~~~~payMchNotify1~~~~~~~~~~~~~~~~~~~"
        println xmlData
        println "#~~~~payMchNotify2~~~~~~~~~~~~~~~~~~~"
        //将XML转为MAP,确保所有字段都参与签名验证
        Map<String,String> mapData = XMLConverUtil.convertToMap(xmlData);
        //转换数据对象
        MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class,xmlData);
        //已处理 去重
        if(expireKey.exists(payNotify.getTransaction_id())){
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("SUCCESS");
            baseResult.setReturn_msg("OK");
            response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
            response.flushBuffer()
            return;
        }
        //签名验证
        if(SignatureUtil.validateSign(mapData,key)){
            //@since 2.8.5
            payNotify.buildDynamicField(mapData);

            expireKey.add(payNotify.getTransaction_id());
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("SUCCESS");
            baseResult.setReturn_msg("OK");
            println "#~~~~payMchNotify3~~~~~~~~~~~~~~~~~~~"
            println payNotify.getCash_fee()
            println "#~~~~payMchNotify4~~~~~~~~~~~~~~~~~~~"
            response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
        }else{
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("FAIL");
            baseResult.setReturn_msg("ERROR");
            response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
        }
        response.flushBuffer()
        return
    }


}
