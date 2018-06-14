<!doctype html>
<html>
<head>
    <meta name="layout" content="wx">
    <title>微信支付</title>
</head>
<body>
<script type="text/javascript">
//调用：
var time1 = new Date().Format("yyyy-MM-dd-hh-mm-ss");
//var time2 = new Date().Format("yyyy-MM-dd hh:mm:ss");
//alert(time1)
var rand = {};
rand.get = function (begin,end){
return Math.floor(Math.random()*(end-begin))+begin;
};
var good = {};
good.title='head_'+ time1;
good.mch_openid ="o8a0q0isswIrAjMRTOqL-nlK56Ao";
good.money =rand.get(1,4);//四分钱以内的随机金额
good.mediaId ="pay_"+ time1;
good.pay_url = "http://wx.bdh114.com/default/pay/wxpayfor.jsp";//支付网关
good.send_url =window.location.href;//如果需要跳转不同页面改成常量 "http://app.chenmin.org/player.html";
good.notify_url = "https://www.tuinai.com.cn/pay/payMchNotifyFor.xml";

</script>
<div class="weui-msg">
    <div class="weui-msg__text-area">
        <h2 class="weui-msg__title">支付早起挑战保证金</h2>
    </div>
</div>
<div class="bd">
    <div class="page__bd">
        <div class="weui-cells__title">保证金说明</div>
        <div class="weui-cells">
            <div class="weui-cell">
                <div class="weui-cell__bd">
                    <p>1.每日打卡时间为6:30-7:30；</p>
                    <p>2.参加需要支付押金参加挑战，每个参与者可最多支付1000元；</p>
                    <p>3.每日完成打卡参与者，大约下午17：30左右可获得平台鼓励金；</p>
                    <p>4.打卡时遇到任何问题，请在当日打卡时间内联系客服微信ipanpan123并提供截图；</p>
                    <p>5.连续完成21天打卡后，可自助退还押金。</p>
                    <a href="javascript:void(0);" class="weui-cell weui-cell_link">
                        <div class="weui-cell__bd">了解更多</div>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="weui-msg">
    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">
            <a href="javascript:pays(1000);"   class="weui-btn weui-btn_primary">支付10元</a>
            <a href="javascript:pays(10000);;"   class="weui-btn weui-btn_primary">支付100元</a>
        </p>
    </div>
    <div class="weui-msg__extra-area">
        <div class="weui-footer">
            <p class="weui-footer__text">Copyright © 2008-2017 www.chenmin.org</p>
        </div>
    </div>
</div>

<form action="" id="pay" method="post">
    <input type="hidden" name="title" id="title" />
    <input type="hidden" name="mch_openid" id="mch_openid" />
    <input type="hidden" name="money" id="money" />
    <input type="hidden" name="openid" id="openid" />
    <input type="hidden" name="mediaId" id="mediaId" />
    <input type="hidden" name="send_url" id="send_url" />
    <input type="hidden" name="notify_url" id="notify_url" />
</form>

<script type="text/javascript">
    function checkMP(){
        var url="//www.tuinai.com.cn/api/wx/user/"+_openid;
        $.ajax({
            async:false,
            type: 'GET',
            url: url,
            statusCode: {
                200: function(json) {
                    checkAPP(json.unionid)
                },
                204:function(){
                    alert("请先关注公众号");
                    wx.closeWindow();
                }
            }
        });
    }

    function checkAPP(unionid){
        var url="//www.tuinai.com.cn/api/wx/unionid/"+unionid;
        $.ajax({
            async:false,
            type: 'GET',
            url: url,
            statusCode: {
                200: function(json) {
                    good.mediaId = good.mediaId + json.openid
                    good.openid =_openid
                    good.send_url = "http://www.tuinai.com.cn/pay/notifyPay?" +
                        "openid="+json.openid + "&money="+good.money
                    update_goods();
                    $("#wx_pay").html("微信支付"+(good.money/100)+"元");
                    $("#wx_pay").click(function(){
                        $("#pay").submit();
                    });
                },
                204:function(){
                    alert("请在公众号中打开小程序一次进行账号激活");
                    wx.closeWindow();
                }
            }
        });
    }
    function update_goods(){
        $("#msgtitle").html(good.title);
        $("#pay").attr("action",good.pay_url);
        $("#mch_openid").val(good.mch_openid);
        $("#money").val(good.money);
        $("#openid").val(good.openid);
        $("#mediaId").val(good.mediaId);
//$("#mediaId").val(good.mediaId+rand.get(1000,9999));
        $("#send_url").val(good.send_url);
        $("#notify_url").val(good.notify_url);
        $("#title").val(good.title);
    }

    function pays(money){
        good.money = money;
        update_goods();
        $("#pay").submit();
    }

    $(function(){
        checkMP();
    });

</script>

</body>
</html>
