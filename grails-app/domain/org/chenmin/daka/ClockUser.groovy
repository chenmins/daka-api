package org.chenmin.daka
/**
 * 打卡用户类
 */
class ClockUser {

    /**
     * 微信公众号的ID
     */
    String openid
    /**
     * 头像地址
     */
    String headImg
    /**
     * 微信昵称
     */
    String nickname
    /**
     * 联合微信全局
     */
    String unionid
    /**
     * 今日打卡时间
     */
    String todayTime
    /**
     * 连续打卡次数
     */
    int staminaCount = 0
    /**
     * 我的投入
     */
    int paid = 0
    /**
     * 我的余额
     */
    int cash  = 0
    /**
     * 累计奖励
     */
    int totalReward  = 0
    /**
     * 下注（每日定时器刷为下注）
     */
    boolean pour = false
    /**
     * 我的冻结余额
     */
    int frozen  = 0

    /**
     * 引导人微信公众号的ID
     */
    String popenid

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 更新时间
     */
    Date lastUpdated

    static mapping = {
        table('daka_clock_user')
    }

    static constraints = {
        todayTime(nullable:true)
        unionid(nullable:true)
        popenid(nullable:true)
    }
}
