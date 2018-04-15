package org.chenmin.daka
/**
 * 结算类
 */
class CalcBoard {
    /**
     * 当前年月日
     */
    String ymd
    /**
     * 实时挑战金
     */
    int currentTotalMoney
    /**
     * 实时参与人数
     */
    int currentParticipateCount
    /**
     * 今日已打卡
     */
    int hitClock
    /**
     * 今日未打卡
     */
    int notHitClock
    /**
     * 今日已打卡金额
     */
    int hitMoney
    /**
     * 今日未打卡金额
     */
    int notHitMoney
    /**
     * 每千份奖励金额
     */
    int thousandRewardMoney
    /**
     * 平台预留
     */
    int cash
    /**
     * 平台补贴
     */
    int paid
    /**
     * 实际瓜分金额
     */
    int reals
    /**
     * 舍掉小数取整
     */
    int floors
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
        table('daka_calc_board')
    }

    static constraints = {
    }
}
