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
     * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金）
     */
    String cashType
    /**
     * 金额（小于零为提现）
     */
    int cash
    /**
     * 备注
     */
    String remark
    /**
     * 创建时间
     */
    Date createTime = new Date()

    static constraints = {
    }
}
