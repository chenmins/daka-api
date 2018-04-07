package org.chenmin.daka
/**
 * 每日看板类
 */
class TodayBoard {

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
     * 早起之星
     */
    ClockUser earlyStar
    /**
     * 最早打卡时间
     */
    String earlyTime
    /**
     * 毅力之星
     */
    ClockUser staminaStar
    /**
     * 毅力次数
     */
    int staminaCount

    static mapping = {
        earlyStar lazy:false
        staminaStar lazy:false
    }

    static constraints = {

    }
}
