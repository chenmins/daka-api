package org.chenmin.daka

import org.springframework.web.bind.annotation.ResponseBody
import weixin.popular.bean.paymch.MchBaseResult
import weixin.popular.bean.paymch.MchPayNotify
import weixin.popular.support.ExpireKey
import weixin.popular.support.expirekey.DefaultExpireKey
import weixin.popular.util.SignatureUtil
import weixin.popular.util.StreamUtils
import weixin.popular.util.XMLConverUtil

import java.nio.charset.Charset

class PayController {

    def index() {
        response.getOutputStream().println("Hello world！"+DateTool.today()+" "+DateTool.time())
        response.flushBuffer()
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
