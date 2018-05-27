package org.chenmin.daka

class FirstReward {

    String openid

    String popenid

    int cash

    int pcash

    String remark

    /**
     * 创建时间
     */
    Date dateCreated

    static mapping = {
        table('daka_first_reward')
    }

    static constraints = {
//        todayTime(nullable:true)
//        unionid(nullable:true)
//        popenid(nullable:true)
    }
}
