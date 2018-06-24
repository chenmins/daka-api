<!doctype html>
<html>
<head>
    <meta name="layout" content="app">
    <title>帐号激活</title>
</head>
<body>

<div class="weui-msg">


    <g:if test="${flash.du}">
        <g:if test="${flash.wu}">
            <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
            <div class="weui-msg__text-area">
                <h2 class="weui-msg__title">帐号激活成功</h2>
                <p class="weui-msg__desc">
                    激活码：${params.unionid}
                </p>
            </div>
        </g:if>
        <g:else>
            <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
            <div class="weui-msg__text-area">
                <h2 class="weui-msg__title">未关注公众号</h2>
                <p class="weui-msg__desc">
                    激活码：${params.unionid}
                </p>
            </div>
        </g:else>
    </g:if>
    <g:else>
        <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
        <div class="weui-msg__text-area">
            <h2 class="weui-msg__title">请点击下方授权</h2>
            <p class="weui-msg__desc">
                激活码：${params.unionid}
            </p>
        </div>
    </g:else>

    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">
            <g:if test="${flash.du}">
                <g:if test="${flash.wu}">
                        请在公众号中点击充值，此打卡游戏仅供测试。
                </g:if>
                <g:else>
                    <a href="javascript:wx.previewImage({urls:[serverBase+'<asset:assetPath src='gh_5fecada2d3b6_258.jpg' />']})" class="weui-btn weui-btn_primary">关注公众号</a>
                </g:else>
            </g:if>
            <g:else>
                <a href="javascript:wx.miniProgram.navigateTo({url: '/pages/login/login'})" class="weui-btn weui-btn_primary">授权登录</a>
            </g:else>
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
