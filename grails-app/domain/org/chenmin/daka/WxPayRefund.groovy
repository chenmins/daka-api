package org.chenmin.daka

class WxPayRefund {

    String appid
    String mch_id
    String nonce_str
    String result_code
    String return_code
    String return_msg
    String sign
    String sign_status

    String err_code
    String err_code_des
    String transaction_id
    String out_trade_no
    String out_refund_no
    String refund_id
    String refund_channel
    int refund_fee
    int total_fee
    int cash_fee
    int cash_refund_fee
    int coupon_refund_fee
    int coupon_refund_count

    /**
     * 创建时间
     */
    Date dateCreated

    static constraints = {
        err_code(nullable:true)
        err_code_des(nullable:true)
        transaction_id(nullable:true)
        out_trade_no(nullable:true)
        out_refund_no(nullable:true)
        refund_id(nullable:true)
        refund_channel(nullable:true)
        refund_fee(nullable:true)
        total_fee(nullable:true)
        cash_fee(nullable:true)
        cash_refund_fee(nullable:true)
        coupon_refund_fee(nullable:true)
        coupon_refund_count(nullable:true)
    }

    static mapping = {
        table('wx_pay_refund')
    }
}
