<!doctype html>
<html>
<head>
    <meta name="layout" content="app">
    <title>帐号激活</title>
</head>
<body>

<div class="weui-msg">



            <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
            <div class="weui-msg__text-area">
                <h2 class="weui-msg__title">帐号激活成功</h2>
                <p class="weui-msg__desc">
                    激活码：${params.unionid}
                </p>
            </div>


        <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
        <div class="weui-msg__text-area">
            <h2 class="weui-msg__title">请点击下方授权</h2>
            <p class="weui-msg__desc">
                激活码：${params.unionid}
            </p>
        </div>

    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">
            <a href="javascript:wx.previewImage({urls:[serverBase+'<asset:assetPath src='1531384201.png' />']})" class="weui-btn weui-btn_primary">微信支付</a>
                <a href="javascript:wx.miniProgram.navigateTo({url: '/pages/login/login'})" class="weui-btn weui-btn_primary">授权登录</a>
<img src="<asset:assetPath src='1531384201.png' />"/>
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
<script>

</script>

</body>
</html>
