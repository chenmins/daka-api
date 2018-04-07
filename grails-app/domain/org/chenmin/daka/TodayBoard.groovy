package org.chenmin.daka
/**
 * 每日看板类
 */
class TodayBoard {

    //当前年月日
    String ymd;
    //实时挑战金
    String currentTotaloney;
    //实时参与人数
    int currentParticipateCount;
    //今日已打卡
    int todayHitClock;
    //今日未打卡
    int todayNotHitClock;
    //早起之星
    ClockUser earlyStar;
    //最早打卡时间
    String earlyTime;
    //毅力之星
    ClockUser staminaStar;
    //毅力次数
    int staminaCount;

    static constraints = {

    }
}
