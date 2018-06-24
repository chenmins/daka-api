package org.chenmin.daka

class WxUser {

//  def a=  '''
//{
//    "city": "",
//    "country": "中国",
//    "groupid": 0,
//    "headimgurl": "http://thirdwx.qlogo.cn/mmopen/PiajxSqBRaEJ13Yib32HfDesxsMT3a6o2vzyA6r8YLzVb56X6eJZhT1gzsTJqVhmB7r7SIzdhDIiak4Kgqiby9V7yA/132",
//    "language": "zh_CN",
//    "nickname": "陈敏",
//    "nickname_emoji": "陈敏",
//    "openid": "oJRLp09Qy8iqsoY08aB2fHC6RoJc",
//    "privilege": null,
//    "province": "北京",
//    "remark": "",
//    "sex": 1,
//    "subscribe": 1,
//    "subscribe_time": 1508407297,
//    "success": true,
//    "tagid_list": [],
//    "unionid": "oTKQls3Uo1PvifaAXPREGwjH1gnA"
//}
//'''

    String openid

    String headimgurl

    String nickname

    String remark

    boolean subscribe

    String subscribe_time

    String unionid

    String popenid

    String punionid
    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 更新时间
     */
    Date lastUpdated

    static mapping = {
        table('wx_user')
    }

    static constraints = {
        remark(nullable:true)
        subscribe(nullable:true)
        subscribe_time(nullable:true)
        unionid(nullable:true)
        popenid(nullable:true)
        punionid(nullable:true)
    }
}
