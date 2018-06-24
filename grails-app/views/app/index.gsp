<!doctype html>
<html>
<head>
    <meta name="layout" content="app">
    <title>登录成功</title>
</head>
<body>

<div class="weui-msg">
    <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
    <div class="weui-msg__text-area">
        <h2 class="weui-msg__title">登录成功</h2>
        <p class="weui-msg__desc">
            unionid：${params.unionid}
        </p>
    </div>
    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">
            <a href="javascript:wx.miniProgram.navigateTo({url: '/pages/login/login'})" class="weui-btn weui-btn_primary">授权登录</a>
            <a href="javascript:wx.previewImage({urls:[serverBase+'<asset:assetPath src='gh_5fecada2d3b6_258.jpg' />']})" class="weui-btn weui-btn_primary">关注公众号</a>
            <g:link controller="pay" action="order" class="weui-btn weui-btn_default" >充值测试</g:link>
            %{--<asset:image src="gh_5fecada2d3b6_258.jpg" />--}%
            %{--<asset:image src="cmcm.png" />--}%
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
