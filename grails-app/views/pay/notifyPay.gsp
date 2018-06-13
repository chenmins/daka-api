<!doctype html>
<html>
<head>
    <meta name="layout" content="wx">
    <title>支付成功</title>
</head>
<body>

<div class="weui-msg">
    <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
    <div class="weui-msg__text-area">
        <h2 class="weui-msg__title">支付成功</h2>
        <p class="weui-msg__desc">
            ￥${params.money.toInteger()/100}
        </p>
    </div>
    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">
            <a href="${createLink(action: 'order')}" class="weui-btn weui-btn_primary">继续充值</a>
            <a href="javascript:wx.closeWindow();" class="weui-btn weui-btn_default">关闭网页</a>
        </p>
    </div>
    <div class="weui-msg__extra-area">
        <div class="weui-footer">
            <p class="weui-footer__links">
                <a href="javascript:void(0);" class="weui-footer__link">底部链接文本</a>
            </p>
            <p class="weui-footer__text">Copyright © 2008-2018 chenmin.org</p>
        </div>
    </div>
</div>


</body>
</html>
