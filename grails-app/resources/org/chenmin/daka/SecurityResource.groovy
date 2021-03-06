package org.chenmin.daka

import grails.converters.JSON
import groovy.sql.Sql
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.http.client.HttpClient
import weixin.popular.api.PayMchAPI
import weixin.popular.bean.paymch.SecapiPayRefund
import weixin.popular.bean.paymch.SecapiPayRefundResult
import weixin.popular.bean.paymch.Transfers
import weixin.popular.bean.paymch.TransfersResult
import weixin.popular.bean.paymch.Unifiedorder
import weixin.popular.bean.paymch.UnifiedorderResult
import weixin.popular.client.LocalHttpClient
import weixin.popular.util.PayUtil
import weixin.popular.util.XMLConverUtil

import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "security", description = "个人安全服务相关接口")
@Path('/api/security')
class SecurityResource {

    def dataSource
    CalcService calcService

    //根据session_key获得解码
    @POST
    @Path('/session_key')
    @ApiOperation(value = "数据解密", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    String session_key(
            @ApiParam(required = true, value = " 临时登录凭证code")
            @FormParam("session_key") String session_key,
            @FormParam("iv") String iv,
            @FormParam("encryptedData") String encryptedData
    ) {
        try {
            String result = AesService.decrypt(encryptedData, session_key, iv, "UTF-8");
            return result
        } catch (Exception e) {
            e.printStackTrace();
        }
        return
    }

    //openid授权
    @GET
    @Path('/jscode2session/{jscode}')
    @ApiOperation(value = "openid授权", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    String jscode2session(
            @ApiParam(required = true, value = " 临时登录凭证code")
            @PathParam("jscode")
                    String jscode
    ){
        //https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        def url = "https://api.weixin.qq.com/sns/jscode2session?"
        url += "appid=wxbd7ee929512fd71f"
        url += "&secret=74492633a33a639fa1301c2ae4310446"
        url += ("&js_code="+jscode)
        url += "&grant_type=authorization_code"
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient();
        def p = new HashMap<String,String>()
        def json = HttpClientTools.get(hc,url,p)

        chc.close()
        return json
    }
//下单
    @GET
    @Path('/scan/{cash}')
    @ApiOperation(value = "扫码支付", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String order2(
                 @ApiParam(required = true, value = "充值金额（单位分）")
                 @PathParam("cash")
                         int cash) {
        String url = "https://www.tuinai.com.cn/pay/PayMchNotify.xml"
        String appid = "wxbd7ee929512fd71f"
        String mch_id = "1490841962"
        String key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2"
//        String wx_openid = openid
        String fee = ""+cash
        Unifiedorder unifiedorder = new Unifiedorder();
        unifiedorder.setAppid(appid);
        unifiedorder.setMch_id(mch_id);
        unifiedorder.setNonce_str(UUID.randomUUID().toString().toString().replace("-", ""));

//        unifiedorder.setOpenid(wx_openid);
        unifiedorder.setBody(""+(cash/100)+"元充值卡");
        unifiedorder.setOut_trade_no(UUID.randomUUID().toString().toString().replace("-", ""));
        unifiedorder.setTotal_fee(fee);//单位分
        unifiedorder.setSpbill_create_ip("127.0.0.1");//IP
        unifiedorder.setNotify_url(url);
        unifiedorder.setTrade_type("NATIVE");//JSAPI，NATIVE，APP，WAP
        unifiedorder.setAttach("scan__"+DateTool.today()+"_"+DateTool.timeOnly());
        //统一下单，生成预支付订单
        UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,key);
        println unifiedorder as JSON
        println unifiedorderResult as JSON
//        //@since 2.8.5  API返回数据签名验证
//        if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status()){
//            String json = PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(), appid, key);
//            return json
//        }
        return unifiedorderResult as JSON
    }
    //下单
    @GET
    @Path('/order/{openid}/{cash}')
    @ApiOperation(value = "下单挑战金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String order(@ApiParam(required = true, value = "微信个人ID")
               @PathParam("openid")
                       String openid,
               @ApiParam(required = true, value = "充值金额（单位分）")
               @PathParam("cash")
                       int cash) {
        String url = "https://www.tuinai.com.cn/pay/PayMchNotify.xml"
        String appid = "wxbd7ee929512fd71f"
        String mch_id = "1490841962"
        String key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2"
        String wx_openid = openid
        String fee = ""+cash
        Unifiedorder unifiedorder = new Unifiedorder();
        unifiedorder.setAppid(appid);
        unifiedorder.setMch_id(mch_id);
        unifiedorder.setNonce_str(UUID.randomUUID().toString().toString().replace("-", ""));

        unifiedorder.setOpenid(wx_openid);
        unifiedorder.setBody("商品信息");
        unifiedorder.setOut_trade_no(UUID.randomUUID().toString().toString().replace("-", ""));
        unifiedorder.setTotal_fee(fee);//单位分
        unifiedorder.setSpbill_create_ip("127.0.0.1");//IP
        unifiedorder.setNotify_url(url);
        unifiedorder.setTrade_type("JSAPI");//JSAPI，NATIVE，APP，WAP
        unifiedorder.setAttach("daka__"+openid+"_"+DateTool.today()+"_"+DateTool.timeOnly());
        //统一下单，生成预支付订单
        UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,key);
        println unifiedorder as JSON
        println unifiedorderResult as JSON
        //@since 2.8.5  API返回数据签名验证
        if(unifiedorderResult.getSign_status() !=null && unifiedorderResult.getSign_status()){
            String json = PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(), appid, key);
            return json
        }
        return unifiedorderResult as JSON
    }
    //支付测试
    @GET
    @Path('/pay/{openid}/{cash}')
    @ApiOperation(value = "支付挑战金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String pay(@ApiParam(required = true, value = "微信个人ID")
               @PathParam("openid")
                       String openid,
               @ApiParam(required = true, value = "充值金额（单位分）")
               @PathParam("cash")
                       int cash) {
        //记录交易流水
        //增加流水数据
        def earlyStar = ClockUser.findByOpenid(openid)
//        def cb1 = new CashBoard()
//        cb1.user = earlyStar
//        cb1.openid = earlyStar.openid
//        cb1.cashType = "deposit"
//        /**
//         * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金）
//         */
//        cb1.cash = cash
//        cb1.remark = "测试付押金"+(cash/100)+"元"
//        cb1.refund = "-1"
//        //TODO 改为真是的
//        cb1.orderID= UUIDTool.getUUID()
//        cb1.save(flush: true)
//        //修改用户押金
//        earlyStar.paid = earlyStar.paid + cash
        //非充值用户打卡下注
        earlyStar.frozen = cash * -1
        earlyStar.save(flush: true)
//        //TODO 更新挑战金
//        int spaid = 0
//        def sql = new Sql(dataSource);
//        String strSql = "select ifnull(sum(paid),0) spaid from daka_clock_user t ";
//        sql.eachRow(strSql) {
//            spaid = it.spaid
//        }
//        // 更新挑战人数
//        int currentCount = 0
//        strSql = "select count(paid) counts from daka_clock_user t where t.paid>0 "
//        sql.eachRow(strSql) {
//            currentCount = it.counts
//        }
//        def hasToday = TodayBoard.findByYmd(DateTool.today())
//        hasToday.currentTotalMoney = spaid
//        //当前挑战人数
//        hasToday.currentParticipateCount=currentCount
//        hasToday.save(flush: true)
        //返回用户对象
        def json =  earlyStar as JSON
        return json
    }



    //结算测试
    @GET
    @Path('/calc/{cash}/{paid}')
    @ApiOperation(value = "计算奖励金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String calc(
            @ApiParam(required = true, value = "预留金额（单位分）")
            @PathParam("cash")
                    int cash,
            @ApiParam(required = true, value = "补贴金额（单位分）")
            @PathParam("paid")
                    int paid) {
            return calcService.calc(cash,paid)
    }


    //打卡测试
    @GET
    @Path('/clock/{openid}')
    @ApiOperation(value = "打卡测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String clock(@ApiParam(required = true, value = "微信个人ID")
                 @PathParam("openid")
                         String openid) {
        //TODO 需要验证是否在合法时间打卡

        //检测打卡记录
        def has = RewardBoard.findByOpenidAndYmd(openid,DateTool.today())
        def earlyStar = ClockUser.findByOpenid(openid)
        if(!has){
            //插入打卡记录
            def r1 = new RewardBoard()
            r1.user = earlyStar
            r1.openid = earlyStar.openid
            r1.ymd = DateTool.today()
            r1.ym = DateTool.month()
            r1.d =DateTool.d()
            r1.paid = earlyStar.paid
            r1.reward = -1
            r1.hitTime = DateTool.time()
            r1.hitType = "wx"
            r1.save(flush: true)
            //打卡成功，修改今日记录
            earlyStar.todayTime = DateTool.time()
            earlyStar.staminaCount = earlyStar.staminaCount+1
            earlyStar.save(flush: true)
        }
        //此处可能会出现并发问题
        int spaid = 0
        def sql = new Sql(dataSource)
        String strSql = "select ifnull(sum(paid),0) spaid from daka_reward_board t where t.ymd='"+DateTool.today()+"'"
        sql.eachRow(strSql) {
            spaid = it.spaid
        }
        int counts = 0
        strSql = "select count(paid) counts from daka_reward_board t where t.ymd='"+DateTool.today()+"'"
        sql.eachRow(strSql) {
            counts = it.counts
        }
        //更新每日显示的打卡人数，和打卡金额
        def today = TodayBoard.findByYmd(DateTool.today())
        today.hitMoney = spaid
        today.notHitMoney =  today.currentTotalMoney - spaid
        today.hitClock = counts
        today.notHitClock = today.currentParticipateCount - counts
        today.save(flush: true)
        sql.close()
        return earlyStar as JSON
    }

    //押金列表 倒排
    @GET
    @Path('/payList/{openid}')
    @ApiOperation(value = "押金列表", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    List<CashBoard> payList(@ApiParam(required = true, value = "微信个人ID")
                   @PathParam("openid")
                           String openid){
        //def pay = CashBoard.findAllByOpenidAndCashTypeAndRefund(openid,"deposit","-1")
        def pay = CashBoard.findAllByOpenidAndCashType(openid,"deposit",[ sort:"id", order:"desc"])
        return pay
    }

    //提现测试
    @GET
    @Path('/take/{openid}/{cash}')
    @ApiOperation(value = "提取奖励金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String take(@ApiParam(required = true, value = "微信个人ID")
               @PathParam("openid")
                       String openid,
               @ApiParam(required = true, value = "提取金额（单位分）")
               @PathParam("cash")
                       int cash) {
        def r = [:]
        if(cash<30){
            r.success = false
            r.msg = "提取奖励金${cash/100}元失败，不能低于0.3元"
            return r as JSON
        }
        def person = ClockUser.findByOpenid(openid)
        if(person.cash<cash){
            r.success = false
            r.msg = "提取奖励金${cash/100}元失败，余额不足"
            return r as JSON
        }
        if(person.frozen>0){
            r.success = false
            r.msg = "提取奖励金${cash/100}元失败，上一笔提现处理中，请稍候"
            return r as JSON
        }
        //select count(*) c from daka_cash_board a where a.cash_type = 'Withdraw' and a.openid = 'oIvCJ5af6xhDnBId5PcCLGFTTAQY'
        // and a.date_created between curdate() and date_sub(curdate(),interval -1 day)
        def sql = new Sql(dataSource)
        int c = 0;
        int max = 1;
        String strSql = "select count(*) c from daka_cash_board a where a.cash_type = 'Withdraw' and a.openid = '"+openid+"' " +
                "and a.date_created between curdate() and date_sub(curdate(),interval -1 day)"
        sql.eachRow(strSql) {
            c = it.c
        }
        if(c>=max){
            r.success = false
            r.msg = "非常抱歉，每日可以提现"+max+"次，请明日再试"
            sql.close()
            return r as JSON
        }else{
            sql.close()
        }

        //dataSource.close()
        String partner_trade_no = "TX" + System.currentTimeMillis();
        //记录资金流水日志
        //增加流水数据
        def cb1 = new CashBoard()
        cb1.user = person
        cb1.openid = person.openid
        cb1.cashType = "Withdraw"
        /**
         * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
         */
        cb1.cash = cash*-1
        cb1.orderID = partner_trade_no
        cb1.remark = "提取奖励金${cash/100}元到微信零钱"
        cb1.save(flush: true)
        //扣除奖励金,进入冻结
        person.cash = person.cash - cash
        person.frozen = cash
        person.save(flush: true)

        //发送支付申请
        String amount= ""+cash;
        String desc= "提取奖励金付款到个人零钱${cash/100}元";


        r.success = true
        r.msg = "提取奖励金${cash/100}元成功"
        try {
            String appid = "wxbd7ee929512fd71f";
            String mch_id = "1490841962";
            String mch_key= "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2";
            String keyStoreFilePath= "/home/bae/app/apiclient_cert.p12";
            Transfers transfers = new Transfers();
            // <mch_appid>wxe062425f740c30d8</mch_appid>
            transfers.setMch_appid(appid);
            // <mchid>10000098</mchid>
            transfers.setMchid(mch_id);
            // <nonce_str>3PG2J4ILTKCH16CQ2502SI8ZNMTM67VS</nonce_str>
            transfers.setNonce_str("NS" + System.currentTimeMillis());
            // <partner_trade_no>100000982014120919616</partner_trade_no>
            transfers.setPartner_trade_no(partner_trade_no);
            // <openid>ohO4Gt7wVPxIT1A9GjFaMYMiZY1s</openid>
            transfers.setOpenid(openid);
            // <check_name>OPTION_CHECK</check_name>
            // NO_CHECK
            transfers.setCheck_name("NO_CHECK");
            // <re_user_name>张三</re_user_name>
            // <amount>100</amount>
            transfers.setAmount(amount);
            // <desc>节日快乐!</desc>
            transfers.setDesc(desc);
            // <spbill_create_ip>10.2.3.10</spbill_create_ip>
            transfers.setSpbill_create_ip("10.2.3.10");
            // <sign>C97BDBACF37622775366F38B629F45E3</sign>
            LocalHttpClient.initMchKeyStore(mch_id, keyStoreFilePath);
            /**
             * 企业付款
             *
             * @param transfers
             *            transfers
             * @param key
             *            key
             * @return TransfersResult
             */
            TransfersResult tr = PayMchAPI.mmpaymkttransfersPromotionTransfers(
                    transfers, mch_key);

            System.out.println( XMLConverUtil.convertToXML(tr));
            System.out.println("getErr_code:"+tr.getErr_code());
            System.out.println("getErr_code_des:"+tr.getErr_code_des());

            //			以下字段在return_code 和result_code都为SUCCESS的时候有返回
            if(tr.getReturn_code().equals("SUCCESS")&&tr.getResult_code().equals("SUCCESS")){
                //TODO 管理员发消息，通知交易成功
                person = ClockUser.findByOpenid(openid)
                person.frozen = 0
                person.save(flush: true)

            }else{
                //TODO 管理员发消息，通知交易失败
                //冻结清空，余额冲正
                person = ClockUser.findByOpenid(openid)
                person.cash = person.cash + cash
                person.frozen = 0
                person.save(flush: true)
                r.success = false
                r.msg = "提取奖励金${cash/100}元失败，"+tr.return_msg
                return r as JSON
            }

            def wpt = new WxPayTransfer()
            wpt.nonce_str = tr.nonce_str
            wpt.result_code= tr.result_code
            wpt.return_code= tr.return_code
            wpt.return_msg= tr.return_msg
            wpt.sign_status= tr.sign_status
            wpt.partner_trade_no= tr.partner_trade_no
            wpt.payment_no= tr.payment_no
            wpt.payment_time= tr.payment_time
            wpt.save(flush: true)
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 管理员发消息，通知交易失败
            //交易失败
            //增加流水数据
            cb1 = new CashBoard()
            cb1.user = person
            cb1.openid = person.openid
            cb1.cashType = "Withdraw"
            /**
             * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
             */
            cb1.cash = cash
            cb1.orderID = partner_trade_no
            cb1.remark = "提取奖励金${cash/100}元失败，冲正到余额"
            cb1.save(flush: true)
            //冻结清空，余额冲正
            person = ClockUser.findByOpenid(openid)
            person.cash = person.cash + cash
            person.frozen = 0
            person.save(flush: true)
        }
        return r as JSON
    }

    //退还挑战金测试
    @GET
    @Path('/refund/{openid}/{orderID}')
    @ApiOperation(value = "退还挑战金测试", notes = "仅供测试")
    @Produces(MediaType.APPLICATION_JSON)
    String refund(@ApiParam(required = true, value = "微信个人ID")
                @PathParam("openid")
                        String openid,
                @ApiParam(required = true, value = "充值")
                @PathParam("orderID")
                        String orderID) {

        def r = [:]
        //今日未结算，暂时不能退款，请在17：30以后尝试退款
        def has = CalcBoard.findByYmd(DateTool.today())
        if(!has){
            r.success = false
            r.msg = "订单${orderID}退款失败，今日未结算，暂时不能退款，请在17：30以后尝试退款！"
            return r as JSON
        }
        //查询订单
        def pay =CashBoard.findByOpenidAndCashTypeAndRefundAndOrderID(openid,"deposit","-1",orderID)
        if(!pay){
            r.success = false
            r.msg = "订单${orderID}退款失败，请误尝试攻击服务器！您的IP和微信已经被记录！"
            return r as JSON
        }
        //退款周期
        int ic = DateTool.getDateSpace(pay.dateCreated,new Date())
        if(ic == 0){
            r.success = false
            r.msg = "订单${orderID}退款失败，刚刚充值，还差21天才可以申请退款！"
            return r as JSON
        }
        int smod = ic % 21
        if(smod != 0){
            r.success = false
            r.msg = "订单${orderID}退款失败，还差${21-smod}天才可以申请退款！"
            return r as JSON
        }
        //查询人员信息
        def person = ClockUser.findByOpenid(openid)

        String appid = "wxbd7ee929512fd71f";
        String mch_id = "1490841962";
        String mch_key = "J8HTUYWLYIPLJLELU3D4GPLNO7FYNFH2";
//        商户订单号	out_trade_no
        String out_trade_no =orderID ;
//        商户退款单号	out_refund_no
        String out_refund_no= "TK" + System.currentTimeMillis();
//        订单金额	total_fee
        Integer total_fee  = pay.cash;
//        退款金额	refund_fee
        Integer refund_fee = pay.cash;

        String keyStoreFilePath= "/home/bae/app/apiclient_cert.p12";
        LocalHttpClient.initMchKeyStore(mch_id, keyStoreFilePath);
        SecapiPayRefund secapiPayRefund = new SecapiPayRefund();
        secapiPayRefund.setNonce_str("NS" + System.currentTimeMillis());
        secapiPayRefund.setAppid(appid);
//        secapiPayRefund.setNotify_url("");
        secapiPayRefund.setMch_id(mch_id);
//
        secapiPayRefund.setOut_trade_no(out_trade_no);
        secapiPayRefund.setOut_refund_no(out_refund_no);
        secapiPayRefund.setTotal_fee(total_fee);
        secapiPayRefund.setRefund_fee(refund_fee);
//        退款原因	refund_desc
//        secapiPayRefund.setrefund
        SecapiPayRefundResult tr = PayMchAPI.secapiPayRefund(secapiPayRefund, mch_key);
        //  <result_code>SUCCESS</result_code>
        if(tr.getResult_code().equals("SUCCESS")){
            //记录腾讯反馈
            def re = new WxPayRefund()
            re.appid = tr.appid
            re. mch_id = tr.mch_id
            re. nonce_str = tr.nonce_str
            re. result_code = tr.result_code
            re. return_code = tr.return_code
            re. return_msg = tr.return_msg
            re. sign = tr.sign
            re. sign_status = tr.sign_status

            re. transaction_id = tr.transaction_id
            re. out_trade_no = tr.out_trade_no
            re. out_refund_no = tr.out_refund_no
            re. refund_id = tr.refund_id
            re. refund_channel = tr.refund_channel
            re. refund_fee = tr.refund_fee
            re. total_fee = tr.total_fee
            re. cash_fee = tr.cash_fee
            re. cash_refund_fee = tr.cash_refund_fee
            re. coupon_refund_fee = tr.cash_refund_fee
            re. coupon_refund_count = tr.coupon_refund_count
            re.save(flush: true)

            //增加流水数据
            def cb1 = new CashBoard()
            cb1.user = person
            cb1.openid = person.openid
            cb1.cashType = "returnDeposit"
            /**
             * 支付类型（deposit ：付押金，reward：发奖励，Withdraw：提现奖励，returnDeposit：退押金,fine：罚款）
             */
            cb1.cash = pay.cash*-1
            cb1.remark = "原路退回${pay.cash/100}元"
            cb1.orderID = tr.out_refund_no
            cb1.save(flush: true)
            //减去押金数据
            person.paid = person.paid - pay.cash
            person.save(flush: true)
            //修改状态
            pay.refund = 0
            pay.save(flush: true)

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

            r.success = true
            r.msg = "订单${orderID}退款成功，请注意查收银行消息预计在24小时内到账"
            return r as JSON
        }else{
            //记录腾讯反馈
            def re = new WxPayRefund()
            re.appid = tr.appid
            re. mch_id = tr.mch_id
            re. nonce_str = tr.nonce_str
            re. result_code = tr.result_code
            re. return_code = tr.return_code
            re. return_msg = tr.return_msg
            re. sign = tr.sign
            re. sign_status = tr.sign_status

            re. err_code = tr.err_code
            re. err_code_des = tr.err_code_des
            re.save(flush: true)

            r.success = false
            r.msg = "订单${orderID}退款失败，"+tr.err_code_des+"，请联系客服处理."
            return r as JSON
        }

    }

}

