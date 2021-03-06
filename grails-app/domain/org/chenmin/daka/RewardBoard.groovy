package org.chenmin.daka
/**
 * 打卡用户奖励类
 */
class RewardBoard {

    /**
     * 打卡用户对象
     */
    ClockUser user
    /**
     * 打卡用户对象ID
     */
    String openid
    /**
     * 打卡日期（年月日）
     */
    String ymd
    /**
     * 打卡日期（年月），日历过滤专用（冗余字段）
     */
    String ym
    /**
     * 打卡属于本月第几日
     */
    int d
    /**
     * 挑战金
     */
    int paid = 0
    /**
     * 打卡奖励金(-1,表示未发放)
     */
    int reward = -1
    /**
     * 打卡时间
     */
    String hitTime
    /**
     * 打卡类型(wx:微信,alipay:支付宝)
     */
    String hitType

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 更新时间
     */
    Date lastUpdated

    static mapping = {
        table('daka_reward_board')
    }

    static constraints = {

    }
}
