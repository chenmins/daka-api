package org.chenmin.daka

import groovy.sql.Sql
import org.springframework.web.bind.annotation.ResponseBody
import weixin.popular.bean.paymch.MchBaseResult
import weixin.popular.bean.paymch.MchPayNotify
import weixin.popular.support.ExpireKey
import weixin.popular.support.expirekey.DefaultExpireKey
import weixin.popular.util.SignatureUtil
import weixin.popular.util.StreamUtils
import weixin.popular.util.XMLConverUtil

import java.nio.charset.Charset

class PayController {

    def dataSource

    def index() {
        response.getOutputStream().println("Hello world "+DateTool.today()+" "+DateTool.time())
        response.flushBuffer()
        return
    }

    //重复通知过滤
    private static ExpireKey expireKey = new DefaultExpireKey();
    private String key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2";	//mch key

    @ResponseBody
    def payMchNotify() {
        //获取请求数据
        String xmlData = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
        println "#~~~~payMchNotify1~~~~~~~~~~~~~~~~~~~"
        println xmlData
        println "#~~~~payMchNotify2~~~~~~~~~~~~~~~~~~~"
        //将XML转为MAP,确保所有字段都参与签名验证
        Map<String,String> mapData = XMLConverUtil.convertToMap(xmlData);
        //转换数据对象
        MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class,xmlData);
        //已处理 去重
        if(expireKey.exists(payNotify.getTransaction_id())){
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("SUCCESS");
            baseResult.setReturn_msg("OK");
            response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
            response.flushBuffer()
            return;
        }
        //签名验证
        if(SignatureUtil.validateSign(mapData,key)){
            //@since 2.8.5
            payNotify.buildDynamicField(mapData);

            expireKey.add(payNotify.getTransaction_id());
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("SUCCESS");
            baseResult.setReturn_msg("OK");
            println "#~~~~payMchNotify3~~~~~~~~~~~~~~~~~~~"
            println payNotify.getCash_fee()
            println "#~~~~payMchNotify4~~~~~~~~~~~~~~~~~~~"
//            #<result_code><![CDATA[SUCCESS]]></result_code>
//            #<return_code><![CDATA[SUCCESS]]></return_code>
            if(payNotify.result_code=='SUCCESS'&& payNotify.return_code=='SUCCESS'){

                def wpt = new WxPayTicket()
                wpt.appid = payNotify.appid
                wpt.attach = payNotify.attach
                wpt.bank_type = payNotify.bank_type
                wpt.cash_fee = payNotify.cash_fee
                wpt.fee_type = payNotify.fee_type
                wpt.is_subscribe = payNotify.is_subscribe
                wpt.mch_id = payNotify.mch_id
                wpt.nonce_str = payNotify.nonce_str
                wpt.openid = payNotify.openid
                wpt.out_trade_no = payNotify.out_trade_no
                wpt.result_code = payNotify.result_code
                wpt.return_code = payNotify.return_code
                wpt.sign = payNotify.sign
                wpt.time_end = payNotify.time_end
                wpt.total_fee = payNotify.total_fee
                wpt.trade_type = payNotify.trade_type
                wpt.transaction_id = payNotify.transaction_id
                wpt.save(flush: true)

                //记录交易流水
                //增加流水数据
                def earlyStar = ClockUser.findByOpenid(payNotify.openid)
                def cb1 = new CashBoard()
                cb1.user = earlyStar
                cb1.openid = earlyStar.openid
                cb1.cashType = "deposit"
                /**
                 * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金）
                 */
                cb1.cash =  wpt.total_fee
                cb1.remark = "付押金"+(wpt.total_fee/100)+"元"
                cb1.refund = "-1"
                cb1.orderID= payNotify.out_trade_no
                cb1.save(flush: true)
                //修改用户押金
                earlyStar.paid = earlyStar.paid + wpt.total_fee
                earlyStar.save(flush: true)
                int spaid = 0
                def sql = new Sql(dataSource);
                String strSql = "select ifnull(sum(paid),0) spaid from daka_clock_user t ";
                sql.eachRow(strSql) {
                    spaid = it.spaid
                }
                // 更新挑战人数
                int currentCount = 0
                strSql = "select count(paid) counts from daka_clock_user t where t.paid>0 "
                sql.eachRow(strSql) {
                    currentCount = it.counts
                }
                def hasToday = TodayBoard.findByYmd(DateTool.today())
                hasToday.currentTotalMoney = spaid
                //当前挑战人数
                hasToday.currentParticipateCount=currentCount
                hasToday.save(flush: true)
                println "#~~~~payMchNotify5~~~~~SUCCESS chenmin~~~~~~~~~~~~~~"
                //核算首冲奖励
                //select count(*) c from daka_cash_board a where a.cash_type ='deposit' and a.openid='oIvCJ5RwdgZQFiM04vIPa0Rq9LvQ';
                int payCount = 0
                strSql = "select count(*) c from daka_cash_board a where a.cash_type ='deposit' and a.openid='"+payNotify.openid+"'"
                println strSql
                sql.eachRow(strSql) {
                    payCount = it.c
                }
                if(payCount==1){
                    //属于第一次充值
                    println "#~~~~payMchNotify6~~~~~first~~~~~~~~~~~~~~"
                    //查询出推荐人ID
                    def u = ClockUser.findByOpenid(payNotify.openid)
                    String popenid =  u.popenid
                    if(popenid){
                        //有推荐人
                        println "#~~~~payMchNotify7~~~~~has popenid~~~~~~~~~~~~~~"
                        FirstReward fr = FirstReward()
                        fr.openid = payNotify.openid
                        fr.popenid = popenid
                        fr.cash = 100
                        fr.pcash = 100
                        fr.remark = '20180527政策，首冲各奖励1元'
                        fr.save(flush: true)
                        //记录首冲人奖励
                        //增加流水数据
                        def cbu = new CashBoard()
                        cbu.user = u
                        cbu.openid = payNotify.openid
                        cbu.cashType = "first"
                        /**
                         * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款，first：首冲奖励）
                         */
                        cbu.cash =100
                        cbu.remark = "首冲奖励${cbu.cash/100}元"
                        cbu.save(flush: true)
                        //记录推荐人奖励
                        def pu =  ClockUser.findByOpenid(popenid)
                        def cbp = new CashBoard()
                        cbp.user = pu
                        cbp.openid = popenid
                        cbp.cashType = "first"
                        /**
                         * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款，first：首冲奖励）
                         */
                        cbp.cash =100
                        cbp.remark = "推荐${u.nickname}首冲奖励${cbp.cash/100}元"
                        cbp.save(flush: true)
                        //变更首冲人余额和累计奖励
                        u.cash = u.cash+cbu.cash
                        u.totalReward = u.totalReward +cbu.cash
                        //变更推荐人余额和累计奖励
                        pu.cash = pu.cash+cbp.cash
                        pu.totalReward = pu.totalReward +cbp.cash
                        u.save(flush: true)
                        pu.save(flush: true)
                        println "#~~~~payMchNotify8~~~~~FirstReward ok~~~~~~~~~~~~~~"

                    }
                }
            }

            response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
        }else{
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("FAIL");
            baseResult.setReturn_msg("ERROR");
            response.getOutputStream().write(XMLConverUtil.convertToXML(baseResult).getBytes());
            println "#~~~~payMchNotify6~~~~~FAIL~~~~~~~~~~~~~~"
        }
        response.flushBuffer()
        return
    }


}
