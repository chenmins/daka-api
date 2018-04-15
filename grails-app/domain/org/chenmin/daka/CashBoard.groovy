package org.chenmin.daka
/**
 * 现金流水类
 */
class CashBoard {

    /**
     * 打卡用户对象
     */
    ClockUser user
    /**
     * 打卡用户对象ID
     */
    String openid
    /**
     * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
     */
    String cashType
    /**
     * 金额（小于零为提现）
     */
    int cash
    /**
     * 退款流水号
     */
    String refund
    /**
     * 充值订单号
     */
    String orderID
    /**
     * 备注
     */
    String remark
    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 更新时间
     */
    Date lastUpdated

    static mapping = {
        table('daka_cash_board')
    }

    static constraints = {
        orderID(nullable:true)
        refund(nullable:true)
    }
}
