<!doctype html>
<html>
<head>
    <meta name="layout" content="wx">
    <title>微信支付1</title>
</head>
<body>
<div id="text1"></div>
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
                    $("#text1").val(json);
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
                    $("#text1").val(json);
                },
                204:function(){
                    alert("请先打开小程序");
                }
            }
        });
    }
    $(function(){
        checkMP();

    });

</script>

</body>
</html>
