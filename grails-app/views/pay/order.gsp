<!doctype html>
<html>
<head>
    <meta name="layout" content="wx">
    <title>微信支付</title>
</head>
<body>
<div id="text1"></div>
<script type="text/javascript">
    var url="//www.tuinai.com.cn/api/wx/user/"+_openid;
    $(function(){
        $.ajax({
            async:false,
            type: 'GET',
            url: url,
            statusCode: {
                200: function() {
                    $("#text1").val(json);
                },
                204:function(){
                    alert("请先关注公众号");
                }
            }
        });
    });

</script>

</body>
</html>
