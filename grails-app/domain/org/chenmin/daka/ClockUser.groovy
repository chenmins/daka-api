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


    static constraints = {
    }
}
