package org.chenmin.daka;

import weixin.popular.api.PayMchAPI;
import weixin.popular.bean.paymch.Transfers;
import weixin.popular.bean.paymch.TransfersResult;
import weixin.popular.client.LocalHttpClient;
import weixin.popular.util.XMLConverUtil;

public class TestTransfer {

    public static void main(String argc[]){
        String openid= "oIvCJ5di1AHrla8j7pQFdTKVlV_U";
        String amount= "30";
        String desc= "测试付款到个人零钱0.3元";

        String appid = "wxbd7ee929512fd71f";
        String mch_id = "1490841962";
        String mch_key= "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2";
        String partner_trade_no = "TX" + System.currentTimeMillis();
        String keyStoreFilePath= "D:\\wx\\apiclient_cert.p12";
// /home/bae/app/apiclient_cert.p12
        Transfers transfers = new Transfers();
        // <mch_appid>wxe062425f740c30d8</mch_appid>
        transfers.setMch_appid(appid);
        // <mchid>10000098</mchid>
        transfers.setMchid(mch_id);
        // <nonce_str>3PG2J4ILTKCH16CQ2502SI8ZNMTM67VS</nonce_str>
        transfers.setNonce_str("NS" + System.currentTimeMillis());
        // <partner_trade_no>100000982014120919616</partner_trade_no>
        transfers.setPartner_trade_no(partner_trade_no);
        // <openid>ohO4Gt7wVPxIT1A9GjFaMYMiZY1s</openid>
        transfers.setOpenid(openid);
        // <check_name>OPTION_CHECK</check_name>
        // NO_CHECK
        transfers.setCheck_name("NO_CHECK");
        // <re_user_name>张三</re_user_name>
        // <amount>100</amount>
        transfers.setAmount(amount);
        // <desc>节日快乐!</desc>
        transfers.setDesc(desc);
        // <spbill_create_ip>10.2.3.10</spbill_create_ip>
        transfers.setSpbill_create_ip("10.2.3.10");
        // <sign>C97BDBACF37622775366F38B629F45E3</sign>
        LocalHttpClient.initMchKeyStore(mch_id, keyStoreFilePath);
        /**
         * 企业付款
         *
         * @param transfers
         *            transfers
         * @param key
         *            key
         * @return TransfersResult
         */
        TransfersResult tr = PayMchAPI.mmpaymkttransfersPromotionTransfers(
                transfers, mch_key);

        System.out.println( XMLConverUtil.convertToXML(tr));
        System.out.println("getErr_code:"+tr.getErr_code());
        System.out.println("getErr_code_des:"+tr.getErr_code_des());
    }
}
