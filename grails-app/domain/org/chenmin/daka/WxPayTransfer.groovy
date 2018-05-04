package org.chenmin.daka

class WxPayTransfer {
    String nonce_str
    String result_code
    String return_code
    String return_msg
    boolean sign_status=false
    String partner_trade_no
    String payment_no
    String payment_time

    /**
     * 创建时间
     */
    Date dateCreated

    static constraints = {
        return_msg(nullable:true)
        partner_trade_no(nullable:true)
        payment_no(nullable:true)
        payment_time(nullable:true)
        attach(nullable:true)
    }

    static mapping = {
        table('wx_pay_transfer')
    }
}
