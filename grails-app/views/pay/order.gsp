<!doctype html>
<html>
<head>
    <meta name="layout" content="wx">
    <title>微信支付1</title>
</head>
<body>
<script type="text/javascript">
//调用：
var time1 = new Date().Format("yyyy-MM-dd-hh-mm");
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
good.pay_url = "http://wx.bdh114.com/default/pay/wxpay.jsp";//支付网关
good.send_url =window.location.href;//如果需要跳转不同页面改成常量 "http://app.chenmin.org/player.html";

</script>
<div class="weui-msg">
    <div class="weui-msg__text-area">
        <h2 class="weui-msg__title">测试的商品1</h2>
        <p class="weui-msg__desc" id="payOK">这里是描述1</p>
        <p>


        </p>
    </div>
    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">

            <a href="javascript:;" id="wx_pay" class="weui-btn weui-btn_primary">微信支付1元</a>
            <a href="http://wx.bdh114.com/default/openid/openid.jsp" id="wx_close" class="weui-btn weui-btn_warn">举报此视频</a>

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
    <input type="hidden" name="mediaId" id="mediaId" />
    <input type="hidden" name="send_url" id="send_url" />
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
                    good.send_url = "http://www.tuinai.com.cn/pay/notifyPay?" +
                        "openid="+json.openid + "money="+good.money
                    update_goods();
                    $("#wx_pay").html("微信支付"+(good.money/100)+"元");
                    $("#wx_pay").click(function(){
                        $("#pay").submit();
                    });
                },
                204:function(){
                    alert("请先打开小程序");
                }
            }
        });
    }
    function update_goods(){
        $("#msgtitle").html(good.title);
        $("#pay").attr("action",good.pay_url);
        $("#mch_openid").val(good.mch_openid);
        $("#money").val(good.money);
        $("#mediaId").val(good.mediaId);
//$("#mediaId").val(good.mediaId+rand.get(1000,9999));
        $("#send_url").val(good.send_url);
        $("#title").val(good.title);
        $("#wx_close").html("微信id:"+_openid);
    }

    $(function(){
        checkMP();

    });

</script>

</body>
</html>
