package org.chenmin.daka;

import weixin.popular.api.PayMchAPI;
import weixin.popular.bean.paymch.SecapiPayRefund;
import weixin.popular.bean.paymch.SecapiPayRefundResult;
import weixin.popular.client.LocalHttpClient;
import weixin.popular.util.XMLConverUtil;

public class TestRefund {
    public static void main(String argc[]) {
        String appid = "wxbd7ee929512fd71f";
        String mch_id = "1490841962";
        String mch_key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2";
//        商户订单号	out_trade_no
        String out_trade_no ="046f18d8623440c49fcc3365f88a50b0" ;
//        商户退款单号	out_refund_no
        String out_refund_no= "TK" + System.currentTimeMillis();
//        订单金额	total_fee
        Integer total_fee  = 100;
//        退款金额	refund_fee
        Integer refund_fee = 100;

        String keyStoreFilePath = "D:\\wx\\apiclient_cert.p12";
        LocalHttpClient.initMchKeyStore(mch_id, keyStoreFilePath);
        SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
        secapiPayRefund.setNonce_str("NS" + System.currentTimeMillis());
        secapiPayRefund.setAppid(appid);
//        secapiPayRefund.setNotify_url("");
        secapiPayRefund.setMch_id(mch_id);
        //TODO 设置secapiPayRefund
//
        secapiPayRefund.setOut_trade_no(out_trade_no);
        secapiPayRefund.setOut_refund_no(out_refund_no);
        secapiPayRefund.setTotal_fee(total_fee);
        secapiPayRefund.setRefund_fee(refund_fee);
//        退款原因	refund_desc
//        secapiPayRefund.setrefund
        SecapiPayRefundResult tr = PayMchAPI.secapiPayRefund(secapiPayRefund, mch_key);
        System.out.println( XMLConverUtil.convertToXML(tr));
        System.out.println("getReturn_code:"+tr.getReturn_code());
        System.out.println("getReturn_msg:"+tr.getReturn_msg());
    }
}
