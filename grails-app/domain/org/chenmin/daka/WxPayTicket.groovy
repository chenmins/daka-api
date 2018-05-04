package org.chenmin.daka

class WxPayTicket {

    String appid
    String attach
    String bank_type
    int cash_fee
    String fee_type
    String is_subscribe
    String mch_id
    String nonce_str
    String openid
    String out_trade_no
    String result_code
    String return_code
    String sign
    String time_end
    int total_fee
    String trade_type
    String transaction_id

    static constraints = {
        attach(nullable:true)
    }

    static mapping = {
        table('wx_pay_ticket')
    }
}
